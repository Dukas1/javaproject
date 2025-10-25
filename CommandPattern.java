import java.util.*;

interface Command {
    void execute();
    void undo();
}

class Light {
    private final String name;
    public Light(String name) { this.name = name; }
    public void on() { System.out.println(name + ": Свет включен."); }
    public void off() { System.out.println(name + ": Свет выключен."); }
}

class Television {
    private final String name;
    public Television(String name) { this.name = name; }
    public void on() { System.out.println(name + ": Телевизор включен."); }
    public void off() { System.out.println(name + ": Телевизор выключен."); }
}

class LightOnCommand implements Command {
    private final Light light;
    public LightOnCommand(Light light) { this.light = light; }
    public void execute() { light.on(); }
    public void undo() { light.off(); }
}

class LightOffCommand implements Command {
    private final Light light;
    public LightOffCommand(Light light) { this.light = light; }
    public void execute() { light.off(); }
    public void undo() { light.on(); }
}

class TelevisionOnCommand implements Command {
    private final Television tv;
    public TelevisionOnCommand(Television tv) { this.tv = tv; }
    public void execute() { tv.on(); }
    public void undo() { tv.off(); }
}

class TelevisionOffCommand implements Command {
    private final Television tv;
    public TelevisionOffCommand(Television tv) { this.tv = tv; }
    public void execute() { tv.off(); }
    public void undo() { tv.on(); }
}

class MacroCommand implements Command {
    private final List<Command> commands;
    public MacroCommand(List<Command> commands) { this.commands = commands; }
    public void execute() { for (Command c : commands) c.execute(); }
    public void undo() {
        ListIterator<Command> it = commands.listIterator(commands.size());
        while (it.hasPrevious()) it.previous().undo();
    }
}

class RemoteControl {
    private final Command[] onCommands;
    private final Command[] offCommands;
    private Command lastCommand;
    private final List<String> log = new ArrayList<>();

    public RemoteControl(int slots) {
        onCommands = new Command[slots];
        offCommands = new Command[slots];
    }

    public void setCommand(int slot, Command onCommand, Command offCommand) {
        if (slot < 0 || slot >= onCommands.length) return;
        onCommands[slot] = onCommand;
        offCommands[slot] = offCommand;
    }

    public void pressOn(int slot) {
        if (!validSlot(slot) || onCommands[slot] == null) return;
        onCommands[slot].execute();
        lastCommand = onCommands[slot];
        log.add("ON slot " + slot + " executed");
    }

    public void pressOff(int slot) {
        if (!validSlot(slot) || offCommands[slot] == null) return;
        offCommands[slot].execute();
        lastCommand = offCommands[slot];
        log.add("OFF slot " + slot + " executed");
    }

    public void pressUndo() {
        if (lastCommand == null) return;
        lastCommand.undo();
        log.add("UNDO executed");
        lastCommand = null;
    }

    public void showLog() {
        for (String s : log) System.out.println(s);
    }

    private boolean validSlot(int slot) {
        return slot >= 0 && slot < onCommands.length;
    }
}

public class CommandPatternDemo {
    public static void main(String[] args) {
        Light livingRoomLight = new Light("Гостиная");
        Television tv = new Television("LG");
        Command lightOn = new LightOnCommand(livingRoomLight);
        Command lightOff = new LightOffCommand(livingRoomLight);
        Command tvOn = new TelevisionOnCommand(tv);
        Command tvOff = new TelevisionOffCommand(tv);
        RemoteControl remote = new RemoteControl(4);
        remote.setCommand(0, lightOn, lightOff);
        remote.setCommand(1, tvOn, tvOff);
        remote.pressOn(0);
        remote.pressOff(0);
        remote.pressUndo();
        remote.pressOn(1);
        remote.pressOff(1);
        List<Command> allOff = Arrays.asList(lightOff, tvOff);
        Command allOffMacro = new MacroCommand(allOff);
        remote.setCommand(2, allOffMacro, null);
        remote.pressOn(2);
        remote.pressUndo();
        remote.showLog();
    }
}
