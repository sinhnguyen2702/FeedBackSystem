package com.gaf.feedbacksystem.service;

import com.gaf.feedbacksystem.dto.ClassDto;
import com.gaf.feedbacksystem.dto.QuestionDto;

import java.util.List;

public interface IQuestionService {
    List<QuestionDto> findAll();
    QuestionDto findById(Integer QuestionId);
    QuestionDto update(QuestionDto questionDto);
    QuestionDto save(QuestionDto questionDto);
    void deleteById(Integer id);
}
