const { app, BrowserWindow, powerSaveBlocker, Menu } = require('electron');
const path = require('path');

let blocker = null;

function createWindow() {
  const win = new BrowserWindow({
    width: 1200,
    height: 800,
    minWidth: 480,
    minHeight: 400,
    backgroundColor: '#072b4a',
    // Set explicitly: the Windows exe is built on Linux without wine, so rcedit
    // never stamps an icon into the binary itself.
    icon: path.join(__dirname, 'build', 'icon.png'),
    autoHideMenuBar: true,
    show: false,
    webPreferences: {
      contextIsolation: true,
      nodeIntegration: false,
      // Without this, Chromium throttles timers whenever the window loses focus,
      // which would quietly wreck the intervals mid-drill.
      backgroundThrottling: false
    }
  });

  win.once('ready-to-show', () => win.show());
  win.loadFile(path.join(__dirname, 'www', 'index.html'));
  return win;
}

app.whenReady().then(() => {
  Menu.setApplicationMenu(null);
  blocker = powerSaveBlocker.start('prevent-display-sleep');
  createWindow();

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) createWindow();
  });
});

app.on('window-all-closed', () => {
  if (blocker !== null && powerSaveBlocker.isStarted(blocker)) powerSaveBlocker.stop(blocker);
  app.quit();
});
