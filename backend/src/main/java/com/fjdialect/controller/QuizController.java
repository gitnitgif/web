package com.fjdialect.controller;

import com.fjdialect.entity.Quiz;
import com.fjdialect.entity.QuizRecord;
import com.fjdialect.service.QuizService;
import com.fjdialect.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz")
@CrossOrigin(origins = "*")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @GetMapping("/list")
    public Result<List<Quiz>> getAllQuizzes() {
        List<Quiz> quizzes = quizService.getAllQuizzes();
        return Result.success("查询成功", quizzes);
    }

    @GetMapping("/city/{cityCode}")
    public Result<List<Quiz>> getQuizzesByCity(@PathVariable String cityCode) {
        List<Quiz> quizzes = quizService.getQuizzesByCityCode(cityCode);
        return Result.success("查询成功", quizzes);
    }

    @GetMapping("/random/{cityCode}")
    public Result<List<Quiz>> getRandomQuizzes(
        @PathVariable String cityCode,
        @RequestParam(defaultValue = "10") Integer limit
    ) {
        List<Quiz> quizzes = quizService.getRandomQuizzes(cityCode, limit);
        return Result.success("查询成功", quizzes);
    }

    @PostMapping("/submit")
    public Result<Map<String, Object>> submitQuiz(
        @RequestBody Map<String, Object> submitData
    ) {
        List<Map<String, String>> answers = (List<Map<String, String>>) submitData.get("answers");
        String cityCode = (String) submitData.get("cityCode");
        Long userId = submitData.get("userId") != null ? Long.valueOf(submitData.get("userId").toString()) : null;
        String username = (String) submitData.get("username");
        
        // 获取随机题目
        List<Quiz> quizzes = quizService.getRandomQuizzes(cityCode, answers.size());
        
        int correctCount = 0;
        int totalScore = 0;
        
        // 判分
        for (Map<String, String> answer : answers) {
            String quizId = answer.get("quizId");
            String userAnswer = answer.get("answer");
            
            for (Quiz quiz : quizzes) {
                if (quiz.getId().toString().equals(quizId)) {
                    if (quiz.getAnswer().equals(userAnswer)) {
                        correctCount++;
                        totalScore += 10;
                    }
                    break;
                }
            }
        }
        
        // 保存记录
        if (userId != null) {
            QuizRecord record = new QuizRecord();
            record.setUserId(userId);
            record.setUsername(username);
            record.setTotalQuestions(answers.size());
            record.setCorrectCount(correctCount);
            record.setScore(totalScore);
            record.setCityCode(cityCode);
            quizService.saveRecord(record);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("correctCount", correctCount);
        result.put("totalQuestions", answers.size());
        result.put("score", totalScore);
        result.put("level", getLevel(totalScore));
        
        return Result.success("提交成功", result);
    }
    
    @GetMapping("/records/{userId}")
    public Result<List<QuizRecord>> getRecords(@PathVariable Long userId) {
        List<QuizRecord> records = quizService.getRecordsByUserId(userId);
        return Result.success("查询成功", records);
    }
    
    private String getLevel(int score) {
        if (score >= 90) return "方言大师";
        if (score >= 70) return "方言能手";
        if (score >= 50) return "方言初学者";
        return "继续加油";
    }
}
