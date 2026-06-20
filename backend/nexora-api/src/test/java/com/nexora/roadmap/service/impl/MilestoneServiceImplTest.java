package com.nexora.roadmap.service.impl;

import com.nexora.common.exception.ApiException;
import com.nexora.roadmap.dto.response.MilestoneProgressResponse;
import com.nexora.roadmap.entity.Milestone;
import com.nexora.roadmap.entity.TaskStatus;
import com.nexora.roadmap.repository.MilestoneRepository;
import com.nexora.roadmap.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MilestoneServiceImplTest {

    @Mock
    private MilestoneRepository milestoneRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private MilestoneServiceImpl milestoneService;

    private UUID milestoneId;
    private Milestone milestone;

    @BeforeEach
    void setUp() {
        milestoneId = UUID.randomUUID();
        milestone = Milestone.builder()
                .title("Database Design")
                .description("Designing relational schemas")
                .sequenceOrder(1)
                .build();
        milestone.setId(milestoneId);
    }

    @Test
    void getMilestoneProgress_ShouldReturnCorrectProgress_WhenTasksExist() {
        // Given
        when(milestoneRepository.findById(milestoneId)).thenReturn(Optional.of(milestone));
        when(taskRepository.countByMilestoneId(milestoneId)).thenReturn(4L);
        when(taskRepository.countByMilestoneIdAndStatus(milestoneId, TaskStatus.COMPLETED)).thenReturn(2L);

        // When
        MilestoneProgressResponse result = milestoneService.getMilestoneProgress(milestoneId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMilestoneId()).isEqualTo(milestoneId);
        assertThat(result.getMilestoneTitle()).isEqualTo("Database Design");
        assertThat(result.getTotalTasks()).isEqualTo(4L);
        assertThat(result.getCompletedTasks()).isEqualTo(2L);
        assertThat(result.getProgressPercentage()).isEqualTo(50.0);

        verify(milestoneRepository).findById(milestoneId);
        verify(taskRepository).countByMilestoneId(milestoneId);
        verify(taskRepository).countByMilestoneIdAndStatus(milestoneId, TaskStatus.COMPLETED);
    }

    @Test
    void getMilestoneProgress_ShouldReturnZeroProgress_WhenNoTasksExist() {
        // Given
        when(milestoneRepository.findById(milestoneId)).thenReturn(Optional.of(milestone));
        when(taskRepository.countByMilestoneId(milestoneId)).thenReturn(0L);
        when(taskRepository.countByMilestoneIdAndStatus(milestoneId, TaskStatus.COMPLETED)).thenReturn(0L);

        // When
        MilestoneProgressResponse result = milestoneService.getMilestoneProgress(milestoneId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMilestoneId()).isEqualTo(milestoneId);
        assertThat(result.getTotalTasks()).isEqualTo(0L);
        assertThat(result.getCompletedTasks()).isEqualTo(0L);
        assertThat(result.getProgressPercentage()).isEqualTo(0.0);

        verify(milestoneRepository).findById(milestoneId);
        verify(taskRepository).countByMilestoneId(milestoneId);
        verify(taskRepository).countByMilestoneIdAndStatus(milestoneId, TaskStatus.COMPLETED);
    }

    @Test
    void getMilestoneProgress_ShouldThrowApiException_WhenMilestoneIdDoesNotExist() {
        // Given
        when(milestoneRepository.findById(milestoneId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> milestoneService.getMilestoneProgress(milestoneId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Milestone not found with id " + milestoneId)
                .satisfies(ex -> {
                    ApiException apiException = (ApiException) ex;
                    assertThat(apiException.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(apiException.getCode()).isEqualTo("MILESTONE_NOT_FOUND");
                });

        verify(milestoneRepository).findById(milestoneId);
        verifyNoInteractions(taskRepository);
    }
}
