/*
Copyright 2024 LinuxSuRen.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package listening.linuxsuren.github.io;

import listening.linuxsuren.github.io.componet.MainPanel;
import listening.linuxsuren.github.io.server.CacheServer;

import javax.swing.*;
import java.io.IOException;

public class Startup {
    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        JFrame frame = new JFrame("Listening");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(new MainPanel());

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new Thread(() -> {
            try {
                new CacheServer().start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

        frame.setLocationRelativeTo(null);
        frame.setSize(1300, 600);
        frame.setVisible(true);
    }
}
