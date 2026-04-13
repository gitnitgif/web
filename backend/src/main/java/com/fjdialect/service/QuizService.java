package com.fjdialect.service;

import com.fjdialect.entity.Quiz;
import com.fjdialect.entity.QuizRecord;
import java.util.List;

public interface QuizService {
    List<Quiz> getAllQuizzes();
    List<Quiz> getQuizzesByCityCode(String cityCode);
    List<Quiz> getRandomQuizzes(String cityCode, Integer limit);
    Quiz getQuizById(Long id);
    void addQuiz(Quiz quiz);
    void deleteQuiz(Long id);
    
    List<QuizRecord> getRecordsByUserId(Long userId);
    void saveRecord(QuizRecord record);
}
