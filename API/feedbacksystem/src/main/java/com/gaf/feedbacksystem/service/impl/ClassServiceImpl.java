package com.gaf.feedbacksystem.service.impl;

import java.util.List;

import com.gaf.feedbacksystem.entity.Assignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gaf.feedbacksystem.dto.ClassDto;
import com.gaf.feedbacksystem.repository.ClazzRepository;
import com.gaf.feedbacksystem.service.IClassService;
import com.gaf.feedbacksystem.utils.ObjectMapperUtils;
import com.gaf.feedbacksystem.entity.Class;

@Service
public class ClassServiceImpl implements IClassService  {

    @Autowired
    ClazzRepository classRepository;
    
	@Override
	public List<ClassDto> findAll() {
		 List<Class> clazz = classRepository.findAll();
	     List<ClassDto> classDtos = ObjectMapperUtils.mapAll(clazz,ClassDto.class);		 
	     return classDtos;
	}

	@Override
	public ClassDto findById(String classId) {
		Class mClass = classRepository.findByClassID(classId);
		ClassDto classDto = ObjectMapperUtils.map(mClass,ClassDto.class);
		return classDto;
	}

	@Override
	public ClassDto update(ClassDto classDto) {
		return null;
	}

	@Override
	public ClassDto save(ClassDto classDto) {
		return null;

	}

	@Override
	public void deleteById(Integer id) {

	}


}
