const gameArea = document.getElementById('gameArea');
const scoreBoard = document.getElementById('scoreBoard');
const levelDisplay = document.getElementById('level');
const livesDisplay = document.getElementById('lives');
const pauseBtn = document.getElementById('pauseBtn');
const pauseMenu = document.getElementById('pauseMenu');
const resumeBtn = document.getElementById('resumeBtn');
const restartBtn = document.getElementById('restartBtn');
const messageOverlay = document.getElementById('messageOverlay');

let score = 0;
let level = 1;
let lives = 3;
let jugsRemaining = 0;
let isPaused = false;

function getRandomPosition() {
  const x = Math.random() * (window.innerWidth - 50);
  const y = Math.random() * (window.innerHeight - 50);
  return { x, y };
}

function showFeedback(text, color, x, y) {
  const fb = document.createElement('div');
  fb.className = 'feedback';
  fb.textContent = text;
  fb.style.color = color;
  fb.style.left = `${x}px`;
  fb.style.top = `${y}px`;
  gameArea.appendChild(fb);
  setTimeout(() => fb.remove(), 500);
}

function showMessage(text, showButton = false) {
  messageOverlay.innerHTML = `<h2>${text}</h2>`;
  if (showButton) {
    const btn = document.createElement('button');
    btn.textContent = 'Restart';
    btn.onclick = () => {
      messageOverlay.style.display = 'none';
      resetGame();
    };
    messageOverlay.appendChild(btn);
  }
  messageOverlay.style.display = 'block';
}

function hideMessage() {
  messageOverlay.style.display = 'none';
}

function createItem(emoji, isJug = false) {
  const item = document.createElement('div');
  item.classList.add('item');
  item.textContent = emoji;

  const pos = getRandomPosition();
  item.style.left = `${pos.x}px`;
  item.style.top = `${pos.y}px`;

  item.addEventListener('click', (e) => {
    if (isPaused) return;
    const rect = item.getBoundingClientRect();
    const clickX = rect.left + rect.width / 2;
    const clickY = rect.top;

    if (isJug) {
      score += 100;
      jugsRemaining--;
      scoreBoard.textContent = `SCORE: ${score}`;
      showFeedback('+100 ✅', 'lime', clickX, clickY);
      item.remove();
      if (jugsRemaining === 0) {
        showMessage(`CONGRATS ON BEATING LEVEL ${level}`);
        setTimeout(() => {
          hideMessage();
          level++;
          levelDisplay.textContent = `Level ${level}`;
          lives = 3;
          updateLives();
          initGame();
        }, 1000);
      }
    } else {
      score -= 50;
      lives--;
      scoreBoard.textContent = `SCORE: ${score}`;
      updateLives();
      showFeedback('-50 ❌', 'red', clickX, clickY);
      if (lives <= 0) {
        showMessage('YOU LOST', true);
      }
    }
  });

  gameArea.appendChild(item);
  floatAround(item);
}

function floatAround(item) {
  let dx = (Math.random() - 0.5) * (level + 1);
  let dy = (Math.random() - 0.5) * (level + 1);

  function move() {
    if (isPaused) {
      requestAnimationFrame(move);
      return;
    }

    let rect = item.getBoundingClientRect();
    let x = rect.left + dx;
    let y = rect.top + dy;

    if (x < 0 || x > window.innerWidth - 30) dx *= -1;
    if (y < 0 || y > window.innerHeight - 30) dy *= -1;

    item.style.left = `${rect.left + dx}px`;
    item.style.top = `${rect.top + dy}px`;

    requestAnimationFrame(move);
  }

  move();
}

function updateLives() {
  livesDisplay.innerHTML = '❤️'.repeat(lives) + '🖤'.repeat(3 - lives);
}

function initGame() {
  document.querySelectorAll('.item').forEach(el => el.remove());
  jugsRemaining = 3 + level;
  const trashCount = 10 + level * 5;
  const trashEmojis = ['🚽️', '🍟', '🥤', '🪣', '🥃', '🍌'];

  for (let i = 0; i < trashCount; i++) {
    const trash = trashEmojis[Math.floor(Math.random() * trashEmojis.length)];
    createItem(trash);
  }

  for (let i = 0; i < jugsRemaining; i++) {
    createItem('🟡💧', true);
  }

  scoreBoard.textContent = `SCORE: ${score}`;
  updateLives();
}

function resetGame() {
  score = 0;
  level = 1;
  lives = 3;
  levelDisplay.textContent = `Level ${level}`;
  updateLives();
  initGame();
}

pauseBtn.addEventListener('click', () => {
  isPaused = true;
  pauseMenu.style.display = 'block';
});

resumeBtn.addEventListener('click', () => {
  isPaused = false;
  pauseMenu.style.display = 'none';
});

restartBtn.addEventListener('click', () => {
  isPaused = false;
  pauseMenu.style.display = 'none';
  resetGame();
});

resetGame();
