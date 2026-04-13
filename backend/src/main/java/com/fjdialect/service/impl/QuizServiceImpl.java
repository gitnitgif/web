package com.fjdialect.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fjdialect.entity.Quiz;
import com.fjdialect.entity.QuizRecord;
import com.fjdialect.mapper.QuizMapper;
import com.fjdialect.mapper.QuizRecordMapper;
import com.fjdialect.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuizServiceImpl implements QuizService {

    @Autowired
    private QuizMapper quizMapper;

    @Autowired
    private QuizRecordMapper quizRecordMapper;

    @Override
    public List<Quiz> getAllQuizzes() {
        return quizMapper.selectList(new LambdaQueryWrapper<Quiz>()
                .orderByDesc(Quiz::getCreateTime));
    }

    @Override
    public List<Quiz> getQuizzesByCityCode(String cityCode) {
        return quizMapper.selectByCityCode(cityCode);
    }

    @Override
    public List<Quiz> getRandomQuizzes(String cityCode, Integer limit) {
        return quizMapper.selectRandom(cityCode, limit);
    }

    @Override
    public Quiz getQuizById(Long id) {
        return quizMapper.selectById(id);
    }

    @Override
    public void addQuiz(Quiz quiz) {
        quiz.setCreateTime(LocalDateTime.now());
        quiz.setUpdateTime(LocalDateTime.now());
        quizMapper.insert(quiz);
    }

    @Override
    public void deleteQuiz(Long id) {
        quizMapper.deleteById(id);
    }

    @Override
    public List<QuizRecord> getRecordsByUserId(Long userId) {
        return quizRecordMapper.selectByUserId(userId);
    }

    @Override
    public void saveRecord(QuizRecord record) {
        record.setCreateTime(LocalDateTime.now());
        quizRecordMapper.insert(record);
    }
}
