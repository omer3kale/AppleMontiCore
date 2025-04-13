import React, { useState, useEffect, useRef } from 'react';
import FootballAutomatonGame from './FootballAutomatonGame';
import Football2DGame from './Football2DGame';

const states = ['KickOff', 'InPlay', 'Goal', 'GameOver'];

const transitions = [
  { from: 'KickOff', event: 'startGame', to: 'InPlay' },
  { from: 'InPlay', event: 'scoreGoal', to: 'Goal' },
  { from: 'InPlay', event: 'endGame', to: 'GameOver' },
  { from: 'Goal', event: 'restart', to: 'InPlay' },
  { from: 'Goal', event: 'endGame', to: 'GameOver' },
];

export default function FootballSimulation() {
  const [currentState, setCurrentState] = useState('KickOff');
  const [log, setLog] = useState([]);
  const [score, setScore] = useState(0);
  const [showTrophy, setShowTrophy] = useState(false);
  const goalAudio = useRef(null);

  useEffect(() => {
    goalAudio.current = new Audio('/assets/goal.mp3');
    goalAudio.current.load();
  }, []);

  const handleEvent = (event) => {
    const transition = transitions.find(
      t => t.from === currentState && t.event === event
    );
    if (transition) {
      setCurrentState(transition.to);
      setLog(log => [...log, `✅ ${transition.from} → ${transition.to} on '${event}'`]);
      if (event === 'scoreGoal') {
        setScore(s => s + 1);
        setShowTrophy(true);
        goalAudio.current?.play().catch(() => {});
        setTimeout(() => setShowTrophy(false), 2000);
      }
    } else {
      setLog(log => [...log, `❌ No transition from ${currentState} on '${event}'`]);
    }
  };

  const resetAutomaton = () => {
    setCurrentState('KickOff');
    setLog(["🔄 Reset to KickOff"]);
    setScore(0);
    setShowTrophy(false);
  };

  return (
    <div className="flex flex-col items-center gap-8 relative">
      <div className="text-center">
        <h2 className="text-xl font-semibold mb-1">🏆 Scoreboard</h2>
        <p className="text-lg">Score: <span className="font-bold text-blue-600">{score}</span></p>
      </div>

      {showTrophy && (
        <div className="absolute top-6 right-6 animate-bounce text-4xl">🏆</div>
      )}

      <FootballAutomatonGame
        states={states}
        transitions={transitions}
        currentState={currentState}
        log={log}
        onEvent={handleEvent}
        onReset={resetAutomaton}
      />

      <Football2DGame
        currentState={currentState}
        onEvent={handleEvent}
      />

      <div className="text-sm text-gray-500 mt-6 max-w-md text-center">
        <p><strong>Rules:</strong> Score goals to increase your points. Use W/A/D to move and Space to shoot.</p>
      </div>
    </div>
  );
}
