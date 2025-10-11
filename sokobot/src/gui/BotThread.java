package gui;

import solver.SokoBotOld;

public class BotThread extends Thread {
  private SokoBotOld sokoBot;
  private int width;
  private int height;
  private char[][] mapData;
  private char[][] itemsData;

  private String solution = null;

  public BotThread(int width, int height, char[][] mapData, char[][] itemsData) {
    sokoBot = new SokoBotOld();
    this.width = width;
    this.height = height;
    this.mapData = mapData;
    this.itemsData = itemsData;
  }

  @Override
  public void run() {
    solution = sokoBot.solveSokobanPuzzle(width, height, mapData, itemsData);
  }

  public String getSolution() {
    return solution;
  }
}
