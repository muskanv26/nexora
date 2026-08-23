import { useNavigate } from 'react-router-dom';

export default function LandingPage() {
  const navigate = useNavigate();

  return (
    <div className="relative isolate overflow-hidden min-h-[calc(100vh-4rem)] flex flex-col justify-center py-12 px-4 sm:px-6 lg:px-8">
      {/* Background glow effects */}
      <div className="absolute inset-x-0 -top-40 -z-10 transform-gpu overflow-hidden blur-3xl sm:-top-80" aria-hidden="true">
        <div className="relative left-[calc(50%-11rem)] aspect-[1155/678] w-[36.125rem] -translate-x-1/2 rotate-[30deg] bg-gradient-to-tr from-brand-600 to-brand-300 opacity-20 sm:left-[calc(50%-30rem)] sm:w-[72.1875rem]"></div>
      </div>

      <div className="max-w-4xl mx-auto text-center animate-fade-in">
        <span className="inline-flex items-center gap-x-2 rounded-full px-3 py-1 text-xs font-semibold leading-5 text-brand-400 bg-brand-500/10 border border-brand-500/20 mb-8 animate-pulse-glow">
          ✨ Evolving Career Guidance with AI
        </span>

        <h1 className="text-4xl sm:text-6xl font-extrabold tracking-tight text-white mb-6 leading-[1.1]">
          Most students don't know how close they are to their{' '}
          <span className="bg-gradient-to-r from-brand-400 via-brand-500 to-brand-300 bg-clip-text text-transparent">
            dream role.
          </span>
        </h1>

        <p className="text-lg sm:text-xl text-slate-400 max-w-2xl mx-auto mb-10 leading-relaxed">
          Nexora analyzes your skills, measures career readiness, and generates a personalized roadmap to help you reach your target role.
        </p>

        <div className="flex flex-col sm:flex-row items-center justify-center gap-4 mb-20">
          <button
            onClick={() => navigate('/career-readiness')}
            className="w-full sm:w-auto px-8 py-4 bg-gradient-to-r from-brand-600 to-brand-500 text-white font-semibold rounded-xl shadow-lg hover:shadow-brand-500/25 hover:from-brand-500 hover:to-brand-600 transition-all duration-300 active:scale-95 text-base border border-brand-400/20"
          >
            Analyze My Career Readiness
          </button>
        </div>

        {/* Features grid */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 text-left max-w-5xl mx-auto">
          {/* Card 1 */}
          <div className="glass-card p-6 flex flex-col justify-between interactive-glow">
            <div>
              <div className="h-10 w-10 rounded-lg bg-brand-500/15 flex items-center justify-center border border-brand-500/20 text-brand-400 font-bold mb-4">
                🎯
              </div>
              <h3 className="text-lg font-semibold text-white mb-2">
                Career Readiness Analysis
              </h3>
              <p className="text-slate-400 text-sm leading-relaxed">
                Evaluate your capabilities relative to industry standard mappings and get a clear readiness percentage.
              </p>
            </div>
          </div>

          {/* Card 2 */}
          <div className="glass-card p-6 flex flex-col justify-between interactive-glow">
            <div>
              <div className="h-10 w-10 rounded-lg bg-brand-500/15 flex items-center justify-center border border-brand-500/20 text-brand-400 font-bold mb-4">
                🛠️
              </div>
              <h3 className="text-lg font-semibold text-white mb-2">
                AI Roadmap Generation
              </h3>
              <p className="text-slate-400 text-sm leading-relaxed">
                Transform any missing skills into a structured milestone-based timeline powered by Gemini AI.
              </p>
            </div>
          </div>

          {/* Card 3 */}
          <div className="glass-card p-6 flex flex-col justify-between interactive-glow">
            <div>
              <div className="h-10 w-10 rounded-lg bg-brand-500/15 flex items-center justify-center border border-brand-500/20 text-brand-400 font-bold mb-4">
                🔍
              </div>
              <h3 className="text-lg font-semibold text-white mb-2">
                Skill Gap Detection
              </h3>
              <p className="text-slate-400 text-sm leading-relaxed">
                Detect exactly which essential libraries, tools, and methodologies are absent from your developer profile.
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Bottom background blur element */}
      <div className="absolute inset-x-0 top-[calc(100vh-20rem)] -z-10 transform-gpu overflow-hidden blur-3xl" aria-hidden="true">
        <div className="relative left-[calc(50%+3rem)] aspect-[1155/678] w-[36.125rem] -translate-x-1/2 bg-gradient-to-tr from-brand-500 to-brand-200 opacity-15 sm:left-[calc(50%+36rem)] sm:w-[72.1875rem]"></div>
      </div>
    </div>
  );
}
