import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';

import HomePage from './HomePage';
import ContactPage from './ContactPage';
import WelcomeBanner from './WelcomeBanner';
import NotFound from './NotFound';
import FootballAutomatonGame from './components/FootballAutomatonGame';

export default function PageRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/home" element={<HomePage />} />
        <Route path="/contact" element={<ContactPage />} />
        <Route path="/welcome" element={<WelcomeBanner name="Guest" />} />
        <Route path="/game" element={<FootballAutomatonGame />} />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </BrowserRouter>
  );
}
