package nl.nibsi.aoc.puzzle.y18.d04;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.regex.*;

import nl.nibsi.aoc.Puzzle;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public final class Day04Puzzle implements Puzzle {

  private static final class ShiftEvent {

    private static enum Type {
      START_OF_SHIFT, GUARD_FALLS_ASLEEP, GUARD_WAKES_UP;
    }

    private static final Pattern PATTERN = Pattern.compile(
      "^\\[(?<timeOfEvent>\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2})\\] (?<eventType>Guard #(?<guardId>\\d+) begins shift|falls asleep|wakes up)$"
    );

    private static final DateTimeFormatter EVENT_TIME_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm", Locale.ROOT);

    private final Type eventType;

    private final LocalDateTime timeOfEvent;

    private String guardId;

    private ShiftEvent(Type eventType, LocalDateTime timeOfEvent, String guardId) {
      if (eventType == null || timeOfEvent == null)
        throw new IllegalArgumentException();

      this.eventType = eventType;
      this.timeOfEvent = timeOfEvent;
      this.guardId = guardId;
    }

    private Type eventType() {
      return eventType;
    }

    private LocalDateTime timeOfEvent() {
      return timeOfEvent;
    }

    private String guardId() {
      return guardId;
    }

    private void setGuardId(String guardId) {
      if (guardId == null)
        throw new IllegalArgumentException();

      this.guardId = guardId.trim();

      if (this.guardId.isEmpty() || !this.guardId.equals(guardId))
        throw new IllegalArgumentException();
    }

    private static ShiftEvent parse(String line) {
      Matcher matcher = PATTERN.matcher(line);
      if (!matcher.matches())
        throw new IllegalArgumentException();

      LocalDateTime timeOfEvent = LocalDateTime.parse(matcher.group("timeOfEvent"), EVENT_TIME_FORMATTER);

      Type eventType;
      String guardId;

      switch (matcher.group("eventType")) {
        case "falls asleep" : return new ShiftEvent(Type.GUARD_FALLS_ASLEEP, timeOfEvent, null);
        case "wakes up"     : return new ShiftEvent(Type.GUARD_WAKES_UP,     timeOfEvent, null);
        default             : return new ShiftEvent(Type.START_OF_SHIFT,     timeOfEvent, matcher.group("guardId"));
      }
    }
  }

  private static int getMinutesOfSleep(List<ShiftEvent> events) {
    int minutesOfSleep = 0;

    LocalDateTime timeOfLastEvent = null;
    for (ShiftEvent event: events) {
      LocalDateTime timeOfCurrentEvent = event.timeOfEvent();

      if (event.eventType() == ShiftEvent.Type.GUARD_WAKES_UP) {
        Duration durationOfSleep = Duration.between(timeOfLastEvent, timeOfCurrentEvent);
        minutesOfSleep += durationOfSleep.toMinutes();
      }

      timeOfLastEvent = timeOfCurrentEvent;
    }

    return minutesOfSleep;
  }

  private static Map<Integer, Integer> getSleepCountsByMinute(List<ShiftEvent> events) {
    Map<Integer, Integer> result = new HashMap<>();

    ShiftEvent lastSleepingEvent = null;
    for (ShiftEvent event: events) {
      switch (event.eventType())
      {
        case GUARD_FALLS_ASLEEP:
          lastSleepingEvent = event;
          break;

        case GUARD_WAKES_UP:
          for (int minute = lastSleepingEvent.timeOfEvent().getMinute(); minute < event.timeOfEvent().getMinute(); minute++)
            result.merge(minute, 1, Integer::sum);

        case START_OF_SHIFT:
          lastSleepingEvent = null;
          break;
      }
    }

    return result;
  }

  private Map<String, List<ShiftEvent>> readEventsByGuard(BufferedReader inputReader) throws IOException {
    List<ShiftEvent> events = inputReader.lines().map(ShiftEvent::parse).collect(toList());
    events.sort(comparing(ShiftEvent::timeOfEvent));

    String guardId = null;
    for (ShiftEvent event: events) {
      switch (event.eventType()) {
        case START_OF_SHIFT:
          guardId = event.guardId(); break;

        case GUARD_FALLS_ASLEEP:
        case GUARD_WAKES_UP:
          event.setGuardId(guardId);
      }
    }

    return events.stream().collect(groupingBy(ShiftEvent::guardId));
  }

  @Override
  public String solveFirstPart(BufferedReader inputReader) throws IOException {
    Map<String, List<ShiftEvent>> eventsByGuard = readEventsByGuard(inputReader);

    Map<String, Integer> minutesOfSleepByGuard = eventsByGuard.entrySet().stream().collect(toMap(Entry::getKey, entry -> getMinutesOfSleep(entry.getValue())));

    String guardWithMostSleep = minutesOfSleepByGuard.entrySet().stream().max(Entry.comparingByValue()).get().getKey();

    Map<Integer, Integer> sleepCountsByMinute = getSleepCountsByMinute(eventsByGuard.get(guardWithMostSleep));

    int mostAsleepMinute = sleepCountsByMinute.entrySet().stream().max(Entry.comparingByValue()).get().getKey();

    return Integer.toString(Integer.parseInt(guardWithMostSleep) * mostAsleepMinute);
  }

  @Override
  public String solveSecondPart(BufferedReader inputReader) throws IOException {
    final class MinuteCountByGuard {
      private String guardId;
      private int minute;
      private int count;

      private MinuteCountByGuard(String guardId, int minute, int count) {
        this.guardId = guardId;
        this.minute = minute;
        this.count = count;
      }

      private String guardId() {
        return guardId;
      }

      private int minute() {
        return minute;
      }

      private int count() {
        return count;
      }
    }

    Map<String, List<ShiftEvent>> eventsByGuard = readEventsByGuard(inputReader);

    Map<Entry<String, Integer>, Integer> sleepingMinuteCounts = eventsByGuard.entrySet().stream().flatMap(eventEntry -> {
      String guardId = eventEntry.getKey();
      List<ShiftEvent> events = eventEntry.getValue();
      Map<Integer, Integer> sleepCountsByMinute = getSleepCountsByMinute(events);
      return sleepCountsByMinute.entrySet().stream().map(minuteEntry -> new MinuteCountByGuard(guardId, minuteEntry.getKey(), minuteEntry.getValue()));
    }).collect(toMap(minuteCount -> new SimpleImmutableEntry<>(minuteCount.guardId, minuteCount.minute), MinuteCountByGuard::count));

    return Integer.toString(sleepingMinuteCounts.entrySet().stream()
      .max(Entry.comparingByValue())
      .map(Entry::getKey)
      .map(entry -> Integer.parseInt(entry.getKey()) * entry.getValue())
      .orElse(-1));
  }
}