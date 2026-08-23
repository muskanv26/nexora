import { Link, useLocation } from 'react-router-dom';

export default function Navbar() {
  const location = useLocation();

  const isActive = (path: string) => location.pathname === path;

  return (
    <nav className="sticky top-0 z-50 w-full bg-slate-950/80 backdrop-blur-md border-b border-slate-800/80">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center">
            <Link to="/" className="flex items-center space-x-2">
              <span className="text-2xl font-bold bg-gradient-to-r from-brand-400 via-brand-500 to-brand-300 bg-clip-text text-transparent tracking-tight">
                Nexora
              </span>
              <span className="text-[10px] uppercase font-bold tracking-widest bg-brand-500/20 text-brand-400 px-1.5 py-0.5 rounded border border-brand-500/30">
                MVP
              </span>
            </Link>
          </div>
          <div className="flex space-x-1 sm:space-x-4">
            <Link
              to="/"
              className={`px-3 py-2 rounded-lg text-sm font-medium transition-colors ${
                isActive('/')
                  ? 'bg-slate-900 text-brand-400'
                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-900/50'
              }`}
            >
              Home
            </Link>
            <Link
              to="/career-readiness"
              className={`px-3 py-2 rounded-lg text-sm font-medium transition-colors ${
                isActive('/career-readiness')
                  ? 'bg-slate-900 text-brand-400'
                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-900/50'
              }`}
            >
              Career Readiness
            </Link>
            <Link
              to="/roadmap-generator"
              className={`px-3 py-2 rounded-lg text-sm font-medium transition-colors ${
                isActive('/roadmap-generator')
                  ? 'bg-slate-900 text-brand-400'
                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-900/50'
              }`}
            >
              Roadmap Generator
            </Link>
          </div>
        </div>
      </div>
    </nav>
  );
}
