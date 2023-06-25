package org.jenjetsu.com.finch;

import org.jenjetsu.com.finch.library.Finch;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;

import static javax.sound.midi.ShortMessage.NOTE_OFF;
import static javax.sound.midi.ShortMessage.NOTE_ON;

public class FinchMidiUtil {
    private static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    private static boolean songPlaying = false;

    private FinchMidiUtil() {}

    public static void playSong(Finch finch, File midiSong) {
        if (songPlaying) return;

        Thread currentThread = new Thread(() -> {
            try {
                Sequence sequence = MidiSystem.getSequence(midiSong);

                Sequencer myseq = MidiSystem.getSequencer();
                myseq.setSequence(sequence);
                double ticksPerSecond = sequence.getResolution() * (myseq.getTempoInBPM() / 60.0);

                int tracksToUseBitmask = 0b11111111;
                if (midiSong.getName().equals("mario1.mid")) {
                    tracksToUseBitmask = 0b00000001;
                }

                Track mainTrack = sequence.createTrack();
                for (int i = 0; i < sequence.getTracks().length; i++) {
                    if ((convertTrackNumToBitPlaceNum(i) & tracksToUseBitmask) != 0) {
                        Track track = sequence.getTracks()[i];
                        for (int j = 0; j < track.size(); j++) {
                            MidiEvent event = track.get(j);
                            mainTrack.add(event);

                        }
                    }
                }

                songPlaying = true;
                long playingTime;
                for (int i = 0; i < mainTrack.size() - 1; i++) {
                    if (!songPlaying) {
                        return;
                    }

                    MidiEvent event = mainTrack.get(i);
                    MidiEvent nextEvent = mainTrack.get(i + 1);
                    MidiMessage message = event.getMessage();
                    if (message instanceof ShortMessage sm && (sm.getCommand() == NOTE_ON || sm.getCommand() == NOTE_OFF)) {
                        playingTime = nextEvent.getTick() - event.getTick();
                        playingTime = playingTime == 20 ? 0 : playingTime;
                        if (sm.getData2() != 0 && sm.getCommand() != NOTE_OFF) {
                            try { //Нужно просто, чтобы продолжало программу после ошибки
                                finch.playNote(sm.getData1(), (double) playingTime / ticksPerSecond);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Thread.sleep((long) (((double) playingTime / ticksPerSecond) * 1000));
                    }
                }
            } catch (InvalidMidiDataException | MidiUnavailableException | IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                songPlaying = false;
            }
        });
        currentThread.setDaemon(true);
        currentThread.start();
    }

    public static void stopCurrentSong() {
        songPlaying = false;
    }

    public static void printInfo(File midiSong) throws InvalidMidiDataException, IOException {
        Sequence sequence = MidiSystem.getSequence(midiSong);

        int trackNumber = 0;
        for (Track track :  sequence.getTracks()) {
            trackNumber++;
            System.out.println("Track " + trackNumber + ": size = " + track.size());
            System.out.println();
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                System.out.print("@" + event.getTick() + " ");
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage sm) {
                    System.out.print("Channel: " + sm.getChannel() + " ");
                    if (sm.getCommand() == NOTE_ON) {
                        int key = sm.getData1();
                        int octave = (key / 12)-1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();
                        System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                    } else if (sm.getCommand() == NOTE_OFF) {
                        int key = sm.getData1();
                        int octave = (key / 12)-1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();
                        System.out.println("Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                    } else {
                        System.out.println("Command:" + sm.getCommand());
                    }
                } else {
                    System.out.println("Other message: " + message.getClass());
                }
            }

            System.out.println();
        }
    }

    private static int convertTrackNumToBitPlaceNum(int trackNum) {
        switch (trackNum) {
            case 0:
                return 0b00000001;
            case 1:
                return 0b00000010;
            case 2:
                return 0b00000100;
            case 3:
                return 0b00001000;
            case 4:
                return 0b00010000;
            case 5:
                return 0b00100000;
            case 6:
                return 0b01000000;
            case 7:
                return 0b10000000;
            default:
                return 0b00000000;
        }
    }

    public static boolean isSongPlaying() {
        return songPlaying;
    }
}
