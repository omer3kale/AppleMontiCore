import React, { useState } from 'react';
import { isState } from './SwiftStyleJSExporter.js';

export default function HomePage() {
  const [input, setInput] = useState('');
  const valid = isState(input);

  return (
    <div className="p-6 rounded-2xl shadow-md max-w-lg mx-auto">
      <h1 className="text-2xl font-bold mb-4">State Checker</h1>
      <input
        className="border p-2 w-full rounded mb-2"
        type="text"
        placeholder="Enter state name"
        value={input}
        onChange={(e) => setInput(e.target.value)}
      />
      <p className={valid ? 'text-green-600' : 'text-red-600'}>
        {valid ? '✅ Valid state' : '❌ Invalid state'}
      </p>
    </div>
  );
}
