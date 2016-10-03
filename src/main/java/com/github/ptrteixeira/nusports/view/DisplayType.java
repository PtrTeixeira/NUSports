package com.github.ptrteixeira.nusports.view;

/**
 * @author Peter Teixeira
 */
public enum DisplayType {
  SCHEDULE,
  STANDINGS;

  @Override
  public String toString() {
    switch (this) {
      case SCHEDULE:
        return "Schedule";
      case STANDINGS:
        return "Standings";
      default:
        throw new IllegalStateException("Unreachable");
    }
  }
}
