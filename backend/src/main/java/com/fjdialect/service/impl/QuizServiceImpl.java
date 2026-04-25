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
    public List<Quiz> getAllQuizzes() {         //查询所有测试类
        return quizMapper.selectList(new LambdaQueryWrapper<Quiz>()
                .orderByDesc(Quiz::getCreateTime));
    }

    @Override
    public List<Quiz> getQuizzesByCityCode(String cityCode) {       //根据城市id检索类
        return quizMapper.selectByCityCode(cityCode);
    }

    @Override
    public List<Quiz> getRandomQuizzes(String cityCode, Integer limit) {       // 获取随机测验类
        return quizMapper.selectRandom(cityCode, limit);
    }

    @Override
    public Quiz getQuizById(Long id) {
        return quizMapper.selectById(id);
    }       //根据id检索类

    @Override
    public void addQuiz(Quiz quiz) {                                        //增加测验类
        quiz.setCreateTime(LocalDateTime.now());
        quiz.setUpdateTime(LocalDateTime.now());
        quizMapper.insert(quiz);
    }

    @Override
    public void deleteQuiz(Long id) {
        quizMapper.deleteById(id);
    }           //删除测验类

    @Override
    public List<QuizRecord> getRecordsByUserId(Long userId) {//查询记录类
        return quizRecordMapper.selectByUserId(userId);
    }

    @Override
    public void saveRecord(QuizRecord record) {             //保存记录类
        record.setCreateTime(LocalDateTime.now());
        quizRecordMapper.insert(record);
    }
}
