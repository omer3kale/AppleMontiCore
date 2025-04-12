import React, { useState } from 'react';

export default function ContactPage() {
  const [message, setMessage] = useState('');
  const [sent, setSent] = useState(false);

  function handleSend() {
    setSent(true);
  }

  return (
    <div className="p-6 rounded-2xl shadow-md max-w-lg mx-auto">
      <h1 className="text-2xl font-bold mb-4">Contact Us</h1>
      <textarea
        className="border p-2 w-full rounded mb-2"
        rows="4"
        placeholder="Your message..."
        value={message}
        onChange={(e) => setMessage(e.target.value)}
      ></textarea>
      <button
        className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
        onClick={handleSend}
      >
        Send Message
      </button>
      {sent && <p className="text-green-600 mt-2">✅ Message sent!</p>}
    </div>
  );
}
