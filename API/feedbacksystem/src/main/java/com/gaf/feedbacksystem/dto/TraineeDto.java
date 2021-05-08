package com.gaf.feedbacksystem.dto;


import com.gaf.feedbacksystem.entity.BaseUserEntity;
import com.gaf.feedbacksystem.entity.Class;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TraineeDto extends BaseUserEntity {
    private String phone;
    private String address;
    private boolean isActive;
    private Integer idSkill;
    private String activationCode;
    private String resetPasswordCode;
    private Collection<Class> classes;
}
