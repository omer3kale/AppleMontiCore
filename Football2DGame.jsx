import React, { useEffect, useRef, useState } from 'react';

export default function Football2DGame() {
  const canvasRef = useRef(null);
  const [keys, setKeys] = useState({});

  const player = useRef({ x: 100, y: 180, dx: 0, dy: 0, jumping: false });
  const opponent = useRef({ x: 400, y: 180 });
  const ball = useRef({ x: 250, y: 190, dx: 0, dy: 0 });

  useEffect(() => {
    const canvas = canvasRef.current;
    const ctx = canvas.getContext('2d');

    const images = {};
    const loadImages = () => {
      const names = ['head', 'shoe', 'ball', 'goal'];
      names.forEach(name => {
        const img = new Image();
        img.src = `/assets/${name}.png`;
        images[name] = img;
      });
    };

    loadImages();

    const gravity = 0.5;

    const drawScene = () => {
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      ctx.fillStyle = '#2ecc71';
      ctx.fillRect(0, 0, canvas.width, canvas.height);

      // goals
      ctx.drawImage(images.goal, 0, 160, 30, 40);
      ctx.drawImage(images.goal, canvas.width - 30, 160, 30, 40);

      // player
      ctx.drawImage(images.head, player.current.x, player.current.y, 20, 20);
      ctx.drawImage(images.shoe, player.current.x, player.current.y + 20, 20, 10);

      // opponent (static for now)
      ctx.drawImage(images.head, opponent.current.x, opponent.current.y, 20, 20);
      ctx.drawImage(images.shoe, opponent.current.x, opponent.current.y + 20, 20, 10);

      // ball
      ctx.drawImage(images.ball, ball.current.x, ball.current.y, 10, 10);
    };

    const update = () => {
      // horizontal
      if (keys['a']) player.current.dx = -2;
      else if (keys['d']) player.current.dx = 2;
      else player.current.dx = 0;

      // jump
      if (keys['w'] && !player.current.jumping) {
        player.current.dy = -8;
        player.current.jumping = true;
      }

      // shoot
      if (keys[' '] && Math.abs(player.current.x - ball.current.x) < 25) {
        ball.current.dx = 5;
        ball.current.dy = -2;
      }

      player.current.x += player.current.dx;
      player.current.y += player.current.dy;

      player.current.dy += gravity;

      if (player.current.y >= 180) {
        player.current.y = 180;
        player.current.dy = 0;
        player.current.jumping = false;
      }

      ball.current.x += ball.current.dx;
      ball.current.y += ball.current.dy;
      ball.current.dy += gravity;

      if (ball.current.y > 190) {
        ball.current.y = 190;
        ball.current.dy *= -0.5;
      }

      if (ball.current.x < 0 || ball.current.x > canvas.width - 10) {
        ball.current.dx *= -1;
      }

      drawScene();
    };

    const loop = () => {
      update();
      requestAnimationFrame(loop);
    };
    loop();

    const downHandler = (e) => setKeys(k => ({ ...k, [e.key]: true }));
    const upHandler = (e) => setKeys(k => ({ ...k, [e.key]: false }));

    window.addEventListener('keydown', downHandler);
    window.addEventListener('keyup', upHandler);

    return () => {
      window.removeEventListener('keydown', downHandler);
      window.removeEventListener('keyup', upHandler);
    };
  }, []);

  return (
    <div className="flex flex-col items-center">
      <h1 className="text-xl font-bold my-2">2D Football Game</h1>
      <canvas ref={canvasRef} width={500} height={250} className="border rounded shadow" />
      <p className="text-sm mt-2">Controls: <kbd>W</kbd> = Jump, <kbd>A</kbd>/<kbd>D</kbd> = Move, <kbd>Space</kbd> = Shoot</p>
    </div>
  );
}
