import React, { useState } from 'react';
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

  const handleEvent = (event) => {
    const transition = transitions.find(
      t => t.from === currentState && t.event === event
    );
    if (transition) {
      setCurrentState(transition.to);
      setLog(log => [...log, `✅ ${transition.from} → ${transition.to} on '${event}'`]);
    } else {
      setLog(log => [...log, `❌ No transition from ${currentState} on '${event}'`]);
    }
  };

  const resetAutomaton = () => {
    setCurrentState('KickOff');
    setLog(["🔄 Reset to KickOff"]);
  };

  return (
    <div className="flex flex-col items-center gap-8">
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
    </div>
  );
}
