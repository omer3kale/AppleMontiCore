import React, { useState } from 'react';

const states = ['KickOff', 'InPlay', 'Goal', 'GameOver'];

const transitions = [
  { from: 'KickOff', event: 'startGame', to: 'InPlay' },
  { from: 'InPlay', event: 'scoreGoal', to: 'Goal' },
  { from: 'InPlay', event: 'endGame', to: 'GameOver' },
  { from: 'Goal', event: 'restart', to: 'InPlay' },
  { from: 'Goal', event: 'endGame', to: 'GameOver' },
];

export default function FootballAutomatonGame() {
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

  const availableEvents = transitions
    .filter(t => t.from === currentState)
    .map(t => t.event);

  return (
    <div className="p-4 max-w-xl mx-auto text-center border rounded-xl shadow bg-white">
      <h1 className="text-2xl font-bold mb-2">🏟️ Football Automaton</h1>
      <p className="text-sm text-gray-500 mb-4">Simulate a game using finite-state logic</p>

      <div className="text-xl mb-2">Current State: <span className="font-mono text-blue-600">{currentState}</span></div>

      <div className="flex flex-wrap gap-2 justify-center my-4">
        {availableEvents.map(event => (
          <button
            key={event}
            className="bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600"
            onClick={() => handleEvent(event)}
          >
            {event}
          </button>
        ))}
        <button
          className="bg-gray-500 text-white px-3 py-1 rounded hover:bg-gray-600"
          onClick={resetAutomaton}
        >
          Reset
        </button>
      </div>

      <div className="text-left mt-4 bg-gray-100 p-3 rounded h-48 overflow-y-auto text-sm font-mono">
        {log.map((entry, i) => (
          <div key={i}>{entry}</div>
        ))}
      </div>
    </div>
  );
}
