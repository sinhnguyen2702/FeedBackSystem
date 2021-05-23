package com.gaf.feedbacksystem.service;

import com.gaf.feedbacksystem.dto.TopicDto;

import java.util.List;

public interface ITopicService {
    List<TopicDto> findAll();

    TopicDto findById(Integer topicId);
}
