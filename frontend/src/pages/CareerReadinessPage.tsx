import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { careerReadinessService } from '../services/careerReadinessService';
import type { CareerReadinessResponse, ReadinessLevel } from '../types';

const POPULAR_ROLES = [
  'Backend Engineer',
  'Frontend Engineer',
  'Full Stack Engineer',
  'ML Engineer',
  'Data Scientist',
  'DevOps Engineer'
];

const SUGGESTED_SKILLS = [
  'Java', 'Spring Boot', 'SQL', 'Git', 'Docker', 'AWS',
  'React', 'TypeScript', 'JavaScript', 'HTML', 'CSS', 'Node.js',
  'Python', 'Machine Learning', 'Data Analysis', 'Kubernetes'
];

export default function CareerReadinessPage() {
  const navigate = useNavigate();

  // Input states
  const [targetRole, setTargetRole] = useState(POPULAR_ROLES[0]);
  const [customRole, setCustomRole] = useState('');
  const [isCustomRole, setIsCustomRole] = useState(false);
  const [skillInput, setSkillInput] = useState('');
  const [skills, setSkills] = useState<string[]>(['Java', 'SQL']);

  // Loading & Error States
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Response state
  const [result, setResult] = useState<CareerReadinessResponse | null>(null);

  // Handlers for role selection
  const handleRoleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const val = e.target.value;
    if (val === 'CUSTOM') {
      setIsCustomRole(true);
      setTargetRole('');
    } else {
      setIsCustomRole(false);
      setTargetRole(val);
    }
  };

  // Handlers for skills tags
  const handleAddSkill = (skill: string) => {
    const trimmed = skill.trim();
    if (trimmed && !skills.some(s => s.toLowerCase() === trimmed.toLowerCase())) {
      setSkills([...skills, trimmed]);
    }
    setSkillInput('');
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter' || e.key === ',') {
      e.preventDefault();
      handleAddSkill(skillInput);
    }
  };

  const handleRemoveSkill = (indexToRemove: number) => {
    setSkills(skills.filter((_, idx) => idx !== indexToRemove));
  };

  // Submit trigger
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const finalRole = isCustomRole ? customRole.trim() : targetRole;

    if (!finalRole) {
      setError('Please specify a target role.');
      return;
    }
    if (skills.length === 0) {
      setError('Please add at least one current skill.');
      return;
    }

    setLoading(true);
    setError(null);
    setResult(null);

    try {
      const response = await careerReadinessService.evaluateCareerReadiness({
        targetRole: finalRole,
        currentSkills: skills,
      });
      setResult(response);
    } catch (err: any) {
      setError(err.message || 'An error occurred during career readiness analysis.');
    } finally {
      setLoading(false);
    }
  };

  // Helper to color readiness level badges
  const getLevelStyles = (level: ReadinessLevel) => {
    switch (level) {
      case 'BEGINNER':
        return 'bg-amber-500/10 text-amber-400 border-amber-500/30';
      case 'INTERMEDIATE':
        return 'bg-blue-500/10 text-blue-400 border-blue-500/30';
      case 'ADVANCED':
        return 'bg-indigo-500/10 text-indigo-400 border-indigo-500/30';
      case 'INTERVIEW_READY':
        return 'bg-emerald-500/10 text-emerald-400 border-emerald-500/30';
      default:
        return 'bg-slate-500/10 text-slate-400 border-slate-500/30';
    }
  };

  // Navigate to Step 2: Auto-populate Roadmap Generator
  const handleProceedToRoadmap = () => {
    if (!result) return;
    const finalRole = isCustomRole ? customRole.trim() : targetRole;

    // Calculate suggested difficulty level mapping
    let suggestedDifficulty = 'INTERMEDIATE';
    if (result.readinessScore <= 30) suggestedDifficulty = 'BEGINNER';
    else if (result.readinessScore >= 80) suggestedDifficulty = 'ADVANCED';

    navigate('/roadmap-generator', {
      state: {
        goal: finalRole,
        currentSkills: skills,
        difficultyLevel: suggestedDifficulty
      }
    });
  };

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-10 animate-fade-in">
      <div className="text-center mb-8">
        <span className="text-xs uppercase font-semibold text-brand-400 tracking-wider bg-brand-500/10 px-2.5 py-1 rounded-full border border-brand-500/25">
          Step 1: Skill Assessment
        </span>
        <h1 className="text-3xl font-extrabold tracking-tight text-white mt-2">
          Career Readiness Evaluation
        </h1>
        <p className="text-slate-400 text-sm mt-1 max-w-xl mx-auto">
          Analyze your capabilities against typical job criteria and review your readiness scores, matched strengths, and outstanding gaps.
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
        {/* Form Container */}
        <form onSubmit={handleSubmit} className="glass-panel p-6 rounded-2xl lg:col-span-5 space-y-6">
          <h2 className="text-lg font-bold text-white border-b border-slate-800 pb-3 flex items-center gap-2">
            <span>📋</span> Assessment Form
          </h2>

          {/* Role selector */}
          <div className="space-y-2">
            <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider">
              Target Role
            </label>
            <select
              onChange={handleRoleChange}
              value={isCustomRole ? 'CUSTOM' : targetRole}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-3 text-slate-200 text-sm focus:outline-none focus:border-brand-500 transition-colors"
            >
              {POPULAR_ROLES.map((role) => (
                <option key={role} value={role}>
                  {role}
                </option>
              ))}
              <option value="CUSTOM">Other (Specify Custom Role)</option>
            </select>

            {isCustomRole && (
              <input
                type="text"
                placeholder="Enter target role (e.g. Cloud Engineer)"
                value={customRole}
                onChange={(e) => setCustomRole(e.target.value)}
                required
                className="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-3 text-slate-200 text-sm focus:outline-none focus:border-brand-500 transition-colors mt-2"
              />
            )}
          </div>

          {/* Current Skills Tag Input */}
          <div className="space-y-2">
            <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider">
              Current Skills
            </label>
            <div className="flex flex-wrap gap-2 p-2 border border-slate-800 bg-slate-950 rounded-xl min-h-[50px] items-center">
              {skills.map((skill, idx) => (
                <span
                  key={idx}
                  className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md text-xs font-semibold bg-brand-500/10 text-brand-300 border border-brand-500/25"
                >
                  {skill}
                  <button
                    type="button"
                    onClick={() => handleRemoveSkill(idx)}
                    className="hover:text-red-400 focus:outline-none font-bold text-[10px]"
                  >
                    ×
                  </button>
                </span>
              ))}
              <input
                type="text"
                placeholder={skills.length === 0 ? "Type skill & press Enter" : "Add more..."}
                value={skillInput}
                onChange={(e) => setSkillInput(e.target.value)}
                onKeyDown={handleKeyDown}
                className="flex-1 min-w-[120px] bg-transparent text-sm text-slate-200 focus:outline-none py-1"
              />
            </div>

            {/* Suggested quick-add skills */}
            <div className="pt-2">
              <span className="text-[10px] text-slate-500 uppercase tracking-wider block mb-1.5">
                Quick Suggestions
              </span>
              <div className="flex flex-wrap gap-1.5 max-h-[100px] overflow-y-auto pr-1">
                {SUGGESTED_SKILLS.map((suggested) => {
                  const exists = skills.some(s => s.toLowerCase() === suggested.toLowerCase());
                  return (
                    <button
                      type="button"
                      key={suggested}
                      disabled={exists}
                      onClick={() => handleAddSkill(suggested)}
                      className={`text-[10px] px-2 py-0.5 rounded transition-all ${
                        exists
                          ? 'bg-slate-900/50 text-slate-700 cursor-not-allowed border border-slate-900'
                          : 'bg-slate-900 text-slate-400 hover:text-white border border-slate-800 hover:border-slate-700'
                      }`}
                    >
                      + {suggested}
                    </button>
                  );
                })}
              </div>
            </div>
          </div>

          {error && (
            <div className="p-3 bg-red-950/30 border border-red-800/50 rounded-xl text-red-400 text-xs">
              ⚠️ {error}
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full py-3.5 bg-gradient-to-r from-brand-600 to-brand-500 text-white font-semibold rounded-xl text-sm hover:from-brand-500 hover:to-brand-600 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed active:scale-95 shadow-lg shadow-brand-600/10 border border-brand-400/10"
          >
            {loading ? 'Analyzing Readiness...' : 'Analyze Readiness'}
          </button>
        </form>

        {/* Results / Empty state Container */}
        <div className="lg:col-span-7">
          {loading && (
            <div className="glass-panel p-12 rounded-2xl flex flex-col items-center justify-center space-y-4 animate-pulse">
              <div className="w-12 h-12 border-4 border-brand-500/20 border-t-brand-400 rounded-full animate-spin"></div>
              <div className="text-center">
                <h3 className="text-white font-semibold">Running Readiness Assessment</h3>
                <p className="text-slate-400 text-xs mt-1">AI matches skills with role expectations and generates action plans...</p>
              </div>
            </div>
          )}

          {!loading && !result && (
            <div className="glass-panel p-12 rounded-2xl text-center border-dashed border-2 border-slate-800 flex flex-col items-center justify-center min-h-[350px]">
              <div className="text-4xl mb-4">🚀</div>
              <h3 className="text-white font-bold text-lg">Ready for Analysis</h3>
              <p className="text-slate-400 text-sm mt-1 max-w-sm">
                Enter your target role and list the skills you currently possess to generate an interactive analysis report.
              </p>
            </div>
          )}

          {!loading && result && (
            <div className="space-y-6 animate-slide-up">
              {/* Score & Profile Card */}
              <div className="glass-panel p-6 rounded-2xl grid grid-cols-1 sm:grid-cols-12 gap-6 items-center">
                {/* SVG Gauge */}
                <div className="sm:col-span-4 flex justify-center">
                  <div className="relative w-32 h-32">
                    <svg className="w-full h-full transform -rotate-90" viewBox="0 0 120 120">
                      {/* Gray Track */}
                      <circle
                        cx="60"
                        cy="60"
                        r="52"
                        className="text-slate-800"
                        strokeWidth="8"
                        stroke="currentColor"
                        fill="transparent"
                      />
                      {/* Active Gauge */}
                      <circle
                        cx="60"
                        cy="60"
                        r="52"
                        className="text-brand-500 transition-all duration-1000 ease-out"
                        strokeWidth="8"
                        strokeDasharray={2 * Math.PI * 52}
                        strokeDashoffset={2 * Math.PI * 52 * (1 - result.readinessScore / 100)}
                        strokeLinecap="round"
                        stroke="currentColor"
                        fill="transparent"
                      />
                    </svg>
                    <div className="absolute inset-0 flex flex-col items-center justify-center">
                      <span className="text-2xl font-extrabold text-white">
                        {result.readinessScore}%
                      </span>
                      <span className="text-[10px] text-slate-400 uppercase tracking-widest font-semibold">
                        Readiness
                      </span>
                    </div>
                  </div>
                </div>

                {/* Score Meta details */}
                <div className="sm:col-span-8 space-y-3">
                  <div>
                    <span className="text-[10px] text-slate-500 uppercase tracking-widest font-bold block">
                      Target Goal
                    </span>
                    <h3 className="text-xl font-bold text-white mt-0.5">
                      {isCustomRole ? customRole : targetRole}
                    </h3>
                  </div>
                  <div>
                    <span className="text-[10px] text-slate-500 uppercase tracking-widest font-bold block mb-1">
                      Readiness Level
                    </span>
                    <span className={`inline-flex px-3 py-1 rounded-md text-xs font-bold border ${getLevelStyles(result.readinessLevel)}`}>
                      {result.readinessLevel.replace('_', ' ')}
                    </span>
                  </div>
                </div>
              </div>

              {/* Summary */}
              <div className="glass-panel p-6 rounded-2xl">
                <h3 className="text-sm font-bold text-slate-300 uppercase tracking-wider mb-2.5">
                  AI Summary
                </h3>
                <p className="text-slate-300 text-sm leading-relaxed">
                  {result.summary}
                </p>
              </div>

              {/* Skills breakdown columns */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {/* Matched Skills */}
                <div className="glass-panel p-5 rounded-2xl border-l-4 border-l-emerald-500">
                  <h4 className="text-sm font-bold text-emerald-400 flex items-center gap-1.5 mb-3">
                    <span>✓</span> Matched Skills ({result.roleMatchedSkills.length})
                  </h4>
                  {result.roleMatchedSkills.length > 0 ? (
                    <div className="flex flex-wrap gap-1.5">
                      {result.roleMatchedSkills.map((s, idx) => (
                        <span key={idx} className="bg-emerald-500/10 text-emerald-300 border border-emerald-500/20 text-xs px-2.5 py-1 rounded-md">
                          {s}
                        </span>
                      ))}
                    </div>
                  ) : (
                    <p className="text-xs text-slate-500 italic">No exact predefined required skills matched yet.</p>
                  )}
                </div>

                {/* Missing Skills */}
                <div className="glass-panel p-5 rounded-2xl border-l-4 border-l-red-500">
                  <h4 className="text-sm font-bold text-red-400 flex items-center gap-1.5 mb-3">
                    <span>✗</span> Missing Skills ({result.missingSkills.length})
                  </h4>
                  {result.missingSkills.length > 0 ? (
                    <div className="flex flex-wrap gap-1.5">
                      {result.missingSkills.map((s, idx) => (
                        <span key={idx} className="bg-red-500/10 text-red-300 border border-red-500/20 text-xs px-2.5 py-1 rounded-md">
                          {s}
                        </span>
                      ))}
                    </div>
                  ) : (
                    <p className="text-xs text-emerald-400 italic">No missing skills detected! You're ready.</p>
                  )}
                </div>
              </div>

              {/* Next Recommended & Priority Action Plan */}
              <div className="grid grid-cols-1 md:grid-cols-12 gap-6">
                {/* Recommended Skills */}
                <div className="glass-panel p-6 rounded-2xl md:col-span-5">
                  <h4 className="text-sm font-bold text-slate-300 uppercase tracking-wider mb-3">
                    Recommended Skills
                  </h4>
                  <ul className="space-y-2">
                    {result.recommendedNextSkills.map((s, idx) => (
                      <li key={idx} className="text-xs text-slate-300 flex items-start gap-2">
                        <span className="text-brand-400">•</span>
                        <span>{s}</span>
                      </li>
                    ))}
                  </ul>
                </div>

                {/* Priority action plan */}
                <div className="glass-panel p-6 rounded-2xl md:col-span-7">
                  <h4 className="text-sm font-bold text-slate-300 uppercase tracking-wider mb-3">
                    Priority Action Plan
                  </h4>
                  <div className="space-y-3.5 relative before:absolute before:left-3 before:top-2 before:bottom-2 before:w-[1px] before:bg-slate-800">
                    {result.priorityActionPlan.map((action, idx) => (
                      <div key={idx} className="flex gap-4 relative">
                        <span className="flex-shrink-0 w-6 h-6 rounded-full bg-slate-950 border border-slate-800 text-[10px] font-bold text-brand-400 flex items-center justify-center z-10">
                          {idx + 1}
                        </span>
                        <p className="text-xs text-slate-300 pt-0.5 leading-relaxed">
                          {action}
                        </p>
                      </div>
                    ))}
                  </div>
                </div>
              </div>

              {/* Guided Step 2 CTA */}
              <div className="p-6 bg-brand-500/10 border border-brand-500/20 rounded-2xl flex flex-col md:flex-row justify-between items-center gap-4">
                <div>
                  <h4 className="text-white font-bold text-sm">Step 2: Generate Your Personalized Roadmap</h4>
                  <p className="text-slate-400 text-xs mt-1">
                    Fill the identified gaps and missing skills. Proceed to construct a structured roadmap layout.
                  </p>
                </div>
                <button
                  onClick={handleProceedToRoadmap}
                  className="w-full md:w-auto px-6 py-3 bg-brand-500 hover:bg-brand-600 active:scale-95 text-white font-semibold text-xs rounded-xl transition-all shadow-lg shadow-brand-500/20 whitespace-nowrap"
                >
                  Generate Roadmap →
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
