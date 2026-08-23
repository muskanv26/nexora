import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import LandingPage from './pages/LandingPage';
import CareerReadinessPage from './pages/CareerReadinessPage';
import RoadmapGeneratorPage from './pages/RoadmapGeneratorPage';

export default function App() {
  return (
    <Router>
      <div className="flex flex-col min-h-screen bg-slate-950 text-slate-100">
        <Navbar />
        <main className="flex-grow">
          <Routes>
            <Route path="/" element={<LandingPage />} />
            <Route path="/career-readiness" element={<CareerReadinessPage />} />
            <Route path="/roadmap-generator" element={<RoadmapGeneratorPage />} />
          </Routes>
        </main>
        <footer className="py-6 border-t border-slate-900/60 text-center text-xs text-slate-500 bg-slate-950/40">
          &copy; {new Date().getFullYear()} Nexora AI. All rights reserved.
        </footer>
      </div>
    </Router>
  );
}
