package com.yogabuddy.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yogabuddy.dto.DailyYogaPlanDTO;
import com.yogabuddy.entity.DailyYogaPlan;
import com.yogabuddy.entity.User;
import com.yogabuddy.entity.YogaPlan;
import com.yogabuddy.repository.DailyYogaPlanRepository;
import com.yogabuddy.repository.YogaPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class YogaAiPlannerService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;
    private final YogaPlanRepository yogaPlanRepository;
    private final DailyYogaPlanRepository dailyYogaPlanRepository;

    public void generateAndSavePlan(String goal, String issue, User user) {

        String finalGoal = mergeGoals(goal);
        String finalIssue = mergeIssues(issue);

        String prompt = buildPrompt(finalGoal, finalIssue);

        String rawResponse = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        try {
            // ✅ Extract just the JSON part (everything inside [...])
            assert rawResponse != null;
            String cleanedJson = rawResponse.replaceAll("(?s)^.*?(\\[.*]).*$", "$1");


            // Optional: check if the response is quoted JSON (e.g., stringified JSON)
            if (cleanedJson.startsWith("\"[") && cleanedJson.endsWith("]\"")) {
                cleanedJson = objectMapper.readValue(cleanedJson, String.class); // unwrap
            }


            System.out.println(cleanedJson);

            // ✅ Parse response into DTO list
            List<DailyYogaPlanDTO> dailyPlanList = objectMapper.readValue(cleanedJson, new TypeReference<>() {});

            // ✅ Validate list
            for (DailyYogaPlanDTO dto : dailyPlanList) {
                if (dto.getDayNumber() < 1 || dto.getDayNumber() > 30 || dto.getPoses() == null || dto.getPoses().isEmpty()) {
                    throw new IllegalArgumentException("Invalid day or missing poses in AI response");
                }
                dto.getPoses().forEach(p -> p.setPoseName(p.getPoseName().replaceAll("[,\\s]+$", "").trim()));
            }

            // ✅ Save YogaPlan
            YogaPlan yogaPlan = new YogaPlan();
            yogaPlan.setUser(user);
            yogaPlan.setCreatedDate(LocalDate.now());
            yogaPlan.setGoal(goal);
            yogaPlan.setIssue(issue);
            yogaPlanRepository.save(yogaPlan);

            System.out.println("Parsed and cleaned daily plans:");
            dailyPlanList.forEach(day -> System.out.println(day.getDayNumber() + ": " + day.getTitle()));

            // ✅ Save each day's plan
            for (DailyYogaPlanDTO dto : dailyPlanList) {
                String posesJson = objectMapper.writeValueAsString(dto.getPoses());

                DailyYogaPlan daily = new DailyYogaPlan();
                daily.setYogaPlan(yogaPlan);
                daily.setDayNumber(dto.getDayNumber());
                daily.setPosesJson(posesJson);
                daily.setTitle(dto.getTitle());

                dailyYogaPlanRepository.save(daily);
            }

        } catch (Exception e) {
//            e.printStackTrace(); // For local dev only
            throw new RuntimeException("Failed to parse or save yoga plan", e);
        }
    }

    private String buildPrompt(String goal, String issue) {
        return String.format("""
You are a certified yoga instructor AI.

Generate a 30-day personalized yoga plan for a user with:
- Goal: %s
- Health Issue: %s

🧘 STRICT FORMAT REQUIREMENTS:
- Return ONLY a valid JSON array containing EXACTLY 30 objects (days).
- Do not include anything other than JSON.
- Each object must include:
  - "dayNumber": (integer from 1 to 30),
  - "title": A unique, creative yoga title for the day (NO numbers, NO word "Day" or "day", NO colons or punctuation at the beginning),
  - "poses": An array of 3 to 6 yoga poses.

📌 Each pose must contain:
  - "poseName": A real **yoga pose name in Sanskrit** (e.g., "Tadasana", "Sukhasana", "Bhujangasana").
  - "duration": A positive integer (in minutes, minimum 1 minute), NOT in quotes.

🕒 TIME CONSTRAINT:
- The total duration (sum of pose durations) for each day must be between **10 and 15 minutes**.
- No individual pose may be less than 1 minute.

🎯 INSTRUCTIONS:
- If multiple goals or issues are provided, balance them across the 30 days.
- Choose poses that serve overlapping purposes where possible.
- Do not overload the plan or exceed the time constraints.
- Pose names must be real, traditional yoga poses from Sanskrit. Do not invent names.
- **ONLY use the following yoga pose names from the provided list:**
    - Tadasana
    - Uttanasana
    - Virabhadrasana I
    - Virabhadrasana II
    - Utthita Trikonasana
    - Utthita Parsvakonasana
    - Ardha Chandrasana
    - Vrikshasana
    - Sukhasana
    - Dandasana
    - Paschimottanasana
    - Janu Sirsasana
    - Baddha Konasana
    - Gomukhasana
    - Virasana
    - Ustrasana
    - Dhanurasana
    - Setu Bandhasana
    - Matsyasana
    - Halasana
    - Sarvangasana
    - Sirsasana
    - Ardha Matsyendrasana
    - Supta Matsyendrasana
    - Balasana
    - Savasana
    - Malasana
    - Utkatasana
    - Navasana
    - Phalakasana
    - Chaturanga Dandasana
    - Urdhva Mukha Svanasana
    - Bakasana
    - Garudasana
    - Anjaneyasana
    - Marjaryasana-Bitilasana
    - Adho Mukha Vrksasana
    - Parsvottanasana
    - Upavistha Konasana
    - Vasisthasana
    - Viparita Karani
    - Eka Pada Rajakapotasana
    - Natarajasana
    - Chakrasana
    - Tittibhasana
    - Salamba Sarvangasana
    - Supta Baddha Konasana
    - Kapotasana
    - Parivrtta Trikonasana
    - Paripurna Navasana
    - Chamatkarasana
    - Parivrtta Parsvakonasana
    - Parivrtta Ardha Chandrasana
    - Parsva Bakasana
    - Akarna Dhanurasana
    - Supta Padangusthasana
    - Urdhva Hastasana
    - Uttana Shishosana
    - Setu Bandhasana Sarvangasana
    - Salamba Sirsasana
    - Ardha Padma Paschimottanasana
    - Eka Pada Koundinyasana I
    - Eka Pada Koundinyasana II
    - Visvamitrasana
    - Astavakrasana
    - Titibasana
    - Salamba Bhujangasana
    - Anantasana
    - Marichyasana A
    - Marichyasana B
    - Marichyasana C
    - Marichyasana D
    - Ardha Bhekasana
    - Bhekasana
    - Vamadevasana
    - Vishnuasana
    - Sukshma Vyayam
    - agnistambhasana
    - Ardha Padma Pasasana
    - Parsva Halasana
    - Padmasana
    - Ardha Uttanasana
    - Vajrasana
    - Salabhasana
    - Utthita Hasta Padangusthasana
    - Prasarita Padottanasana
    - Kumbhakasana
    - Mayurasana
    - Pincha Mayurasana
    - Ardha Baddha Padmottanasana
    - Ashtanga Namaskara
    - Urdhva Dhanurasana
    - Pashasana
    - Supta Virasana
    - Krounchasana
    - Chakki Chalanasana
    - Naukasana
    - Makarasana
    - Surya Namaskar
    - Chandra Namaskar
    - Yoga Mudrasana
    - Urdhva Prasrta Eka Padasana
    - Parsva Dandasana
    - Bhairavasana
    - Digasana
    - Bhujapidasana
    - Tolasana
    - Titli Asana
- Avoid repeating the same set of poses across multiple days.
- Each title must be **different** from all other titles.
- Output must start with "[" and end with "]" with no text before or after.

🚫 DO NOT:
- Include any explanation, description, headings, or markdown.
- Use "Day", numbers, or any punctuation like ":" in the title field.
- Return anything outside of valid, parsable JSON.

✅ OUTPUT EXAMPLE (follow format strictly):
[
  {
    "dayNumber": 1,
    "title": "Gentle Flexibility Flow",
    "poses": [
      { "poseName": "Sukhasana", "duration": 5 },
      { "poseName": "Marjaryasana", "duration": 4 },
      { "poseName": "Balasana", "duration": 3 }
    ]
  },
  ...
]
""", goal, issue);
    }


    public String mergeGoals(String rawGoals) {
        if (rawGoals == null || rawGoals.isBlank()) return "General fitness";

        String[] goals = rawGoals.split(",");
        List<String> trimmed = Arrays.stream(goals)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .distinct()
                .collect(Collectors.toList());

        if (trimmed.isEmpty()) return "General fitness";

        // Join intelligently
        if (trimmed.size() == 1) {
            return "Improve " + trimmed.getFirst();
        } else {
            String last = trimmed.removeLast();
            return "Improve " + String.join(", ", trimmed) + " and " + last;
        }
    }

    public String mergeIssues(String rawIssues) {
        if (rawIssues == null || rawIssues.isBlank()) return "No major health issues";

        String[] issues = rawIssues.split(",");
        List<String> trimmed = Arrays.stream(issues)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .distinct()
                .collect(Collectors.toList());

        if (trimmed.isEmpty()) return "No major health issues";

        // Join intelligently
        if (trimmed.size() == 1) {
            return "Issue related to " + trimmed.getFirst();
        } else {
            String last = trimmed.removeLast();
            return "Postural or health concerns including " + String.join(", ", trimmed) + " and " + last;
        }
    }



}
