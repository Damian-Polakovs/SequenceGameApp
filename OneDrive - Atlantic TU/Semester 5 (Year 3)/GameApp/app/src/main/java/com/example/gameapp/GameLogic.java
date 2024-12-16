package com.example.gameapp;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class GameLogic {
    private static final int INITIAL_SEQUENCE_LENGTH = 4;
    private static final int SEQUENCE_INCREMENT = 2;

    private List<Integer> sequence;
    private int currentRound;
    private int sequenceLength;
    private Random random;

    public GameLogic() {
        sequence = new ArrayList<>();
        currentRound = 1;
        sequenceLength = INITIAL_SEQUENCE_LENGTH;
        random = new Random();
        generateSequence();
    }

    public GameLogic(int initialLength) {
        sequence = new ArrayList<>();
        currentRound = 1;
        sequenceLength = initialLength;
        random = new Random();
        generateSequence();
    }

    public void generateSequence() {
        sequence.clear();
        for (int i = 0; i < sequenceLength; i++) {
            sequence.add(random.nextInt(4));
        }
    }

    public List<Integer> generateSequence(int length) {
        List<Integer> newSequence = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            newSequence.add(random.nextInt(4));
        }
        sequence = newSequence;
        return sequence;
    }

    public List<Integer> getSequence() {
        return new ArrayList<>(sequence);
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public int getSequenceLength() {
        return sequenceLength;
    }

    public void incrementRound() {
        currentRound++;
        sequenceLength += SEQUENCE_INCREMENT;
        generateSequence();
    }

    public boolean checkSequence(List<Integer> playerSequence) {
        if (playerSequence.size() != sequence.size()) {
            return false;
        }
        return playerSequence.equals(sequence);
    }

    public int getColorAt(int index) {
        if (index >= 0 && index < sequence.size()) {
            return sequence.get(index);
        }
        return -1;
    }

    public int calculateScore() {
        return sequenceLength;
    }

    public void resetGame() {
        currentRound = 1;
        sequenceLength = INITIAL_SEQUENCE_LENGTH;
        generateSequence();
    }
}