/*
 * Copyright 2024 LinuxSuRen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package listening.linuxsuren.github.io.cli;

import listening.linuxsuren.github.io.server.CacheServer;
import org.apache.commons.cli.*;

import java.io.IOException;

public class CacheServerCLI {
    private static final int defaultPort = 8080;

    public static void main(String[] args) {
        // create the command line parser
        CommandLineParser parser = new DefaultParser();

        // create the Options
        Options options = new Options();
        options.addOption("p", "port", true, "The cache server port");

        int port = 0;
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);

            String portStr = line.getOptionValue("port", "" + defaultPort);

            port = Integer.parseInt(portStr);
        } catch (ParseException exp) {
            System.out.println("Unexpected exception:" + exp.getMessage());
        } catch (NumberFormatException e) {
            port = defaultPort;
            System.out.println("got invalid port number:" + e.getMessage());
        }

        try {
            new CacheServer().start(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
