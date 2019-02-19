package main;

import java.io.IOException;

/**
 * WindowsCommander
 * Using windows PowerShell/Nircmd we can control certain pc actions
 * ex. Logging out, restarting, muting audio, ...
 */
public class WindowsPSCommander {
    private static Process pProcess = null;

    public WindowsPSCommander() {
    }

    /**
     * InvokePSCommand
     * @param cmd
     */
    public void InvokePSCommand(String cmd) {
        try {
            pProcess = new ProcessBuilder("powershell.exe", cmd).start();
        } catch (IOException e) {
        }
    }

    /**
     * InvokeNirCommand
     * @param commands
     * Utility method, used to run nircmd commands
     * the string array is used for command parameters
     */
    public void InvokeNirCommand(String[] commands) {
        try {
            if (commands.length == 1) {
                pProcess = new ProcessBuilder("deps/nircmd.exe", commands[0]).start();
            } else if (commands.length == 2) {
                pProcess = new ProcessBuilder("deps/nircmd.exe", commands[0], commands[1]).start();
            } else if (commands.length == 3) {
                pProcess = new ProcessBuilder("deps/nircmd.exe", commands[0], commands[1], commands[2]).start();
            } else if (commands.length == 4) {
                pProcess = new ProcessBuilder("deps/nircmd.exe", commands[0], commands[1], commands[2], commands[3]).start();
            } else if (commands.length == 5) {
                pProcess = new ProcessBuilder("deps/nircmd.exe", commands[0], commands[1], commands[2], commands[3], commands[4]).start();
            }
        } catch (IOException e) {
        }
    }

    /**
     * RunExecutable
     * @param command
     * use nircmd to run the executable in the provided directory
     *
     *  STATUS:
     *  Working
     */
    public void RunExecutable(String command) {
        try {
            pProcess = new ProcessBuilder("deps/nircmd.exe", "exec", "show", command).start();
        } catch (IOException e) {
        }
    }

    /**
     * Lock the pc
     *  STATUS:
     *  Working
     */
    public void LockPC() {
        String lock = "rundll32.exe user32.dll,LockWorkStation";
        this.InvokePSCommand(lock);
    }

    /**
     * Logout of the current user
     * STATUS:
     */
    public void Logout() {
        String[] commands = {"", ""};
        commands[0] = "exitwin";
        commands[1] = "logoff";
        this.InvokeNirCommand(commands);
    }

    /**
     * Put the pc to sleep
     * STATUS:
     * Working
     */
    public void Sleep() {
        String[] command = {""};
        command[0] = "standby";
        this.InvokeNirCommand(command);
    }

    /**
     * Shutdown the PC
     * TODO:
     * Add a confirmation check
     *  STATUS:
     */
    public void Shutdown() {
        String[] commands = {"", ""};
        commands[0] = "exitwin";
        commands[1] = "poweroff";
        this.InvokeNirCommand(commands);
    }

    /**
     * Restart the PC
     * TODO:
     * Add a confirmation check
     *  STATUS:
     */
    public void Restart() {
        String[] commands = {"", ""};
        commands[0] = "exitwin";
        commands[1] = "reboot";

        this.InvokeNirCommand(commands);
    }

    /**
     * Increase the system volume
     * TODO:
     * handle increase and decrease volume
     * Increase the system volume by 2000 units (out of 65535)	nircmd.exe changesysvolume 2000
     * Decrease the system volume by 5000 units (out of 65535)	nircmd.exe changesysvolume -5000
     */
    public void ChangeVolume(String amount) {
        String[] commands = {"", ""};
        commands[0] = "changesysvolume";
        commands[1] = amount;
        this.InvokeNirCommand(commands);
    }

    /**
     * Mute the system volume
     *  STATUS:
     *  Working
     */
    public void Mute() {
        String[] commands = {"", ""};
        commands[0] = "mutesysvolume";
        commands[1] = "1";
        this.InvokeNirCommand(commands);
    }

    /**
     * Unmute the system volume
     *  STATUS:
     *  Working
     */
    public void Unmute() {
        String[] commands = {"", ""};
        commands[0] = "mutesysvolume";
        commands[1] = "0";
        this.InvokeNirCommand(commands);

    }
}
