import { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { roadmapService } from '../services/roadmapService';
import type { GenerateRoadmapResponse, DifficultyLevel } from '../types';

export default function RoadmapGeneratorPage() {
  const location = useLocation();
  const state = location.state as { goal?: string; currentSkills?: string[]; difficultyLevel?: string } | null;

  // Form states prefilled if state exists
  const [goal, setGoal] = useState('');
  const [skillInput, setSkillInput] = useState('');
  const [skills, setSkills] = useState<string[]>([]);
  const [difficulty, setDifficulty] = useState<DifficultyLevel>('INTERMEDIATE');
  const [timelineMonths, setTimelineMonths] = useState(6);

  // Load from location.state if redirected from readiness evaluation
  useEffect(() => {
    if (state) {
      if (state.goal) setGoal(state.goal);
      if (state.currentSkills) setSkills(state.currentSkills);
      if (state.difficultyLevel) {
        const level = state.difficultyLevel as DifficultyLevel;
        if (['BEGINNER', 'INTERMEDIATE', 'ADVANCED'].includes(level)) {
          setDifficulty(level);
        }
      }
    }
  }, [state]);

  // Loading & Error States for generation
  const [generating, setGenerating] = useState(false);
  const [generateError, setGenerateError] = useState<string | null>(null);

  // Response roadmap data
  const [roadmap, setRoadmap] = useState<GenerateRoadmapResponse | null>(null);

  // Saving states
  const [saving, setSaving] = useState(false);
  const [saveSuccess, setSaveSuccess] = useState<string | null>(null);
  const [saveError, setSaveError] = useState<string | null>(null);

  // Accordion active tracker (index of open milestone)
  const [openMilestoneIdx, setOpenMilestoneIdx] = useState<number | null>(0);

  // Skill tags handlers
  const handleAddSkill = () => {
    const trimmed = skillInput.trim();
    if (trimmed && !skills.some(s => s.toLowerCase() === trimmed.toLowerCase())) {
      setSkills([...skills, trimmed]);
    }
    setSkillInput('');
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter' || e.key === ',') {
      e.preventDefault();
      handleAddSkill();
    }
  };

  const handleRemoveSkill = (idxToRemove: number) => {
    setSkills(skills.filter((_, idx) => idx !== idxToRemove));
  };

  // Generate handler
  const handleGenerate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!goal.trim()) {
      setGenerateError('Please enter a learning goal or target role.');
      return;
    }

    setGenerating(true);
    setGenerateError(null);
    setRoadmap(null);
    setSaveSuccess(null);
    setSaveError(null);

    try {
      const response = await roadmapService.generateRoadmap({
        goal: goal.trim(),
        currentSkills: skills,
        difficultyLevel: difficulty,
        timelineMonths,
      });
      setRoadmap(response);
      setOpenMilestoneIdx(0); // Open first milestone by default
    } catch (err: any) {
      setGenerateError(err.message || 'An error occurred during roadmap generation.');
    } finally {
      setGenerating(false);
    }
  };

  // Save handler
  const handleSaveRoadmap = async () => {
    if (!roadmap) return;
    setSaving(true);
    setSaveSuccess(null);
    setSaveError(null);

    try {
      const response = await roadmapService.persistRoadmap(roadmap);
      setSaveSuccess(`Roadmap "${response.title}" successfully saved with ID: ${response.id}`);
    } catch (err: any) {
      setSaveError(err.message || 'An error occurred while saving the roadmap.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-10 animate-fade-in">
      <div className="text-center mb-8">
        <span className="text-xs uppercase font-semibold text-brand-400 tracking-wider bg-brand-500/10 px-2.5 py-1 rounded-full border border-brand-500/25">
          Step 2: Roadmap Generation
        </span>
        <h1 className="text-3xl font-extrabold tracking-tight text-white mt-2">
          Personalized Learning Roadmap
        </h1>
        <p className="text-slate-400 text-sm mt-1 max-w-xl mx-auto">
          Convert your target career goal and outstanding gaps into an actionable milestone-based study plan.
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
        {/* Form panel */}
        <form onSubmit={handleGenerate} className="glass-panel p-6 rounded-2xl lg:col-span-5 space-y-5">
          <h2 className="text-lg font-bold text-white border-b border-slate-800 pb-3 flex items-center gap-2">
            <span>⚙️</span> Roadmap Configuration
          </h2>

          {/* Goal Input */}
          <div className="space-y-1.5">
            <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider">
              Goal / Target Role
            </label>
            <input
              type="text"
              placeholder="e.g. Backend Software Engineer"
              value={goal}
              onChange={(e) => setGoal(e.target.value)}
              required
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-3 text-slate-200 text-sm focus:outline-none focus:border-brand-500 transition-colors"
            />
          </div>

          {/* Skills tags */}
          <div className="space-y-1.5">
            <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider">
              Skills Already Known
            </label>
            <div className="flex flex-wrap gap-2 p-2 border border-slate-800 bg-slate-950 rounded-xl min-h-[50px] items-center">
              {skills.map((skill, idx) => (
                <span
                  key={idx}
                  className="inline-flex items-center gap-1 px-2 py-0.5 rounded-md text-xs font-semibold bg-brand-500/10 text-brand-300 border border-brand-500/25"
                >
                  {skill}
                  <button
                    type="button"
                    onClick={() => handleRemoveSkill(idx)}
                    className="hover:text-red-400 focus:outline-none text-[10px]"
                  >
                    ×
                  </button>
                </span>
              ))}
              <input
                type="text"
                placeholder={skills.length === 0 ? "Type skill & Enter" : "Add..."}
                value={skillInput}
                onChange={(e) => setSkillInput(e.target.value)}
                onKeyDown={handleKeyDown}
                className="flex-1 min-w-[100px] bg-transparent text-sm text-slate-200 focus:outline-none py-1"
              />
            </div>
          </div>

          {/* Difficulty Level Buttons */}
          <div className="space-y-1.5">
            <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider">
              Difficulty Level
            </label>
            <div className="grid grid-cols-3 gap-2">
              {(['BEGINNER', 'INTERMEDIATE', 'ADVANCED'] as DifficultyLevel[]).map((level) => (
                <button
                  type="button"
                  key={level}
                  onClick={() => setDifficulty(level)}
                  className={`py-2 text-xs font-bold rounded-lg border transition-all ${
                    difficulty === level
                      ? 'bg-brand-500/10 text-brand-400 border-brand-500/50'
                      : 'bg-slate-950 border-slate-800 text-slate-400 hover:text-slate-200'
                  }`}
                >
                  {level}
                </button>
              ))}
            </div>
          </div>

          {/* Timeline Months Input */}
          <div className="space-y-1.5">
            <div className="flex justify-between text-xs font-semibold text-slate-300 uppercase tracking-wider">
              <span>Timeline Limit</span>
              <span className="text-brand-400 font-bold">{timelineMonths} Months</span>
            </div>
            <input
              type="range"
              min="1"
              max="36"
              value={timelineMonths}
              onChange={(e) => setTimelineMonths(parseInt(e.target.value))}
              className="w-full h-1 bg-slate-800 rounded-lg appearance-none cursor-pointer accent-brand-500"
            />
            <div className="flex justify-between text-[10px] text-slate-500">
              <span>1 Month</span>
              <span>12 Mos</span>
              <span>24 Mos</span>
              <span>36 Months</span>
            </div>
          </div>

          {generateError && (
            <div className="p-3 bg-red-950/30 border border-red-800/50 rounded-xl text-red-400 text-xs">
              ⚠️ {generateError}
            </div>
          )}

          <button
            type="submit"
            disabled={generating}
            className="w-full py-3.5 bg-gradient-to-r from-brand-600 to-brand-500 text-white font-semibold rounded-xl text-sm hover:from-brand-500 hover:to-brand-600 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed active:scale-95 shadow-lg shadow-brand-600/10 border border-brand-400/10"
          >
            {generating ? 'Generating Roadmap...' : 'Generate Roadmap'}
          </button>
        </form>

        {/* Output Panel */}
        <div className="lg:col-span-7">
          {generating && (
            <div className="glass-panel p-12 rounded-2xl flex flex-col items-center justify-center space-y-4 animate-pulse">
              <div className="w-12 h-12 border-4 border-brand-500/20 border-t-brand-400 rounded-full animate-spin"></div>
              <div className="text-center">
                <h3 className="text-white font-semibold">Generating Your Roadmap</h3>
                <p className="text-slate-400 text-xs mt-1">Gemini AI is structuring your study modules and learning timeline...</p>
              </div>
            </div>
          )}

          {!generating && !roadmap && (
            <div className="glass-panel p-12 rounded-2xl text-center border-dashed border-2 border-slate-800 flex flex-col items-center justify-center min-h-[350px]">
              <div className="text-4xl mb-4">🗺️</div>
              <h3 className="text-white font-bold text-lg">No Roadmap Generated</h3>
              <p className="text-slate-400 text-sm mt-1 max-w-sm">
                Submit the configuration panel to have our AI construct a custom week-by-week visual curriculum path.
              </p>
            </div>
          )}

          {!generating && roadmap && (
            <div className="space-y-6 animate-slide-up">
              {/* Header and save panel */}
              <div className="glass-panel p-6 rounded-2xl flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
                <div>
                  <span className="text-[10px] text-slate-500 uppercase tracking-widest font-bold block">
                    Generated Roadmap
                  </span>
                  <h3 className="text-xl font-extrabold text-white mt-0.5">
                    {roadmap.roadmapTitle}
                  </h3>
                  <div className="flex gap-2.5 mt-2">
                    <span className="text-[10px] px-2 py-0.5 rounded bg-brand-500/10 text-brand-300 border border-brand-500/20 uppercase font-semibold">
                      {roadmap.difficultyLevel}
                    </span>
                    <span className="text-[10px] px-2 py-0.5 rounded bg-slate-900 text-slate-400 border border-slate-800 font-semibold">
                      {roadmap.timelineMonths} Months
                    </span>
                  </div>
                </div>

                <button
                  type="button"
                  onClick={handleSaveRoadmap}
                  disabled={saving}
                  className="w-full sm:w-auto px-5 py-2.5 bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-bold rounded-xl transition-all shadow-md active:scale-95 disabled:opacity-50"
                >
                  {saving ? 'Saving...' : 'Save Roadmap'}
                </button>
              </div>

              {saveSuccess && (
                <div className="p-4 bg-emerald-950/30 border border-emerald-800/40 rounded-xl text-emerald-400 text-xs font-medium">
                  🎉 {saveSuccess}
                </div>
              )}

              {saveError && (
                <div className="p-4 bg-red-950/30 border border-red-800/40 rounded-xl text-red-400 text-xs font-medium">
                  ⚠️ {saveError}
                </div>
              )}

              {/* Milestones Accordions */}
              <div className="space-y-3">
                {roadmap.milestones.map((milestone, mIdx) => {
                  const isOpen = openMilestoneIdx === mIdx;
                  const totalEstHours = milestone.tasks.reduce((sum, t) => sum + t.estimatedHours, 0);

                  return (
                    <div
                      key={mIdx}
                      className={`glass-panel rounded-xl overflow-hidden border-l-2 transition-all ${
                        isOpen ? 'border-l-brand-500 bg-slate-900/50' : 'border-l-slate-800'
                      }`}
                    >
                      {/* Accordion Trigger */}
                      <button
                        onClick={() => setOpenMilestoneIdx(isOpen ? null : mIdx)}
                        className="w-full p-5 text-left flex justify-between items-center focus:outline-none hover:bg-slate-900/30 transition-colors"
                      >
                        <div className="space-y-1">
                          <span className="text-[9px] uppercase tracking-widest font-bold text-brand-400">
                            Milestone {mIdx + 1}
                          </span>
                          <h4 className="text-sm font-bold text-white">
                            {milestone.milestoneTitle}
                          </h4>
                          <p className="text-slate-400 text-xs line-clamp-1">
                            {milestone.milestoneDescription}
                          </p>
                        </div>
                        <div className="flex items-center gap-3">
                          <span className="text-[10px] px-2 py-0.5 bg-slate-950 text-slate-400 rounded-md border border-slate-800 whitespace-nowrap">
                            ⏱️ {totalEstHours} hrs
                          </span>
                          <span className="text-slate-500 font-bold transition-transform duration-300">
                            {isOpen ? '▲' : '▼'}
                          </span>
                        </div>
                      </button>

                      {/* Accordion Content */}
                      {isOpen && (
                        <div className="p-5 border-t border-slate-900 bg-slate-950/20 space-y-4">
                          <div>
                            <span className="text-[9px] text-slate-500 uppercase tracking-widest font-bold block mb-1">
                              Overview Description
                            </span>
                            <p className="text-xs text-slate-300 leading-relaxed">
                              {milestone.milestoneDescription}
                            </p>
                          </div>

                          {/* Tasks list */}
                          <div className="space-y-3">
                            <span className="text-[9px] text-slate-500 uppercase tracking-widest font-bold block">
                              Task Actions List ({milestone.tasks.length})
                            </span>
                            <div className="grid grid-cols-1 gap-2.5">
                              {milestone.tasks.map((task, tIdx) => (
                                <div key={tIdx} className="p-3.5 bg-slate-950/60 border border-slate-900 rounded-lg flex items-start justify-between gap-4">
                                  <div className="space-y-1">
                                    <h5 className="text-xs font-bold text-white flex items-center gap-2">
                                      <span className="w-1.5 h-1.5 rounded-full bg-brand-400"></span>
                                      {task.taskTitle}
                                    </h5>
                                    <p className="text-[11px] text-slate-400 pl-3.5 leading-relaxed">
                                      {task.taskDescription}
                                    </p>
                                  </div>
                                  <span className="text-[9px] px-1.5 py-0.5 bg-brand-500/10 text-brand-300 border border-brand-500/20 rounded font-semibold whitespace-nowrap">
                                    {task.estimatedHours} hrs
                                  </span>
                                </div>
                              ))}
                            </div>
                          </div>
                        </div>
                      )}
                    </div>
                  );
                })}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
