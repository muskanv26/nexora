export interface ApiError {
  message: string;
  code: string;
  details?: Array<{
    field: string;
    message: string;
    rejectedValue: any;
  }>;
}

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  error?: ApiError;
  timestamp: string;
}

export type DifficultyLevel = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED';

export type ReadinessLevel = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED' | 'INTERVIEW_READY';

export interface CareerReadinessRequest {
  targetRole: string;
  currentSkills: string[];
}

export interface CareerReadinessResponse {
  readinessScore: number;
  readinessLevel: ReadinessLevel;
  roleMatchedSkills: string[];
  missingSkills: string[];
  recommendedNextSkills: string[];
  priorityActionPlan: string[];
  summary: string;
}

export interface GenerateRoadmapRequest {
  goal: string;
  currentSkills?: string[];
  difficultyLevel: DifficultyLevel;
  timelineMonths: number;
}

export interface GenerateTaskResponse {
  taskTitle: string;
  taskDescription: string;
  estimatedHours: number;
}

export interface GenerateMilestoneResponse {
  milestoneTitle: string;
  milestoneDescription: string;
  tasks: GenerateTaskResponse[];
}

export interface GenerateRoadmapResponse {
  generatedForGoal: string;
  difficultyLevel: DifficultyLevel;
  timelineMonths: number;
  roadmapTitle: string;
  milestones: GenerateMilestoneResponse[];
}

export interface RoadmapResponse {
  id: string;
  title: string;
  description: string;
  difficultyLevel: DifficultyLevel;
  estimatedWeeks: number;
  createdAt: string;
  updatedAt: string;
}
