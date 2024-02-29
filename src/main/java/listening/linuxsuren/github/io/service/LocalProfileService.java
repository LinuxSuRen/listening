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

package listening.linuxsuren.github.io.service;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.util.ArrayList;

public class LocalProfileService implements ProfileService{
    private String configFile = System.getProperty("user.home") + File.separator + ".config/listening/profile.yaml";

    @Override
    public Profile getProfile() throws IOException {
        Representer representer = new Representer(new DumperOptions());
        representer.getPropertyUtils().setSkipMissingProperties(true);
        LoaderOptions loaderOptions = new LoaderOptions();
        Yaml yaml = new Yaml(new Constructor(Profile.class, loaderOptions), representer);

        try (InputStream input = new FileInputStream(configFile)) {
            return yaml.load(input);
        }
    }

    @Override
    public void addQueue(ToDoEpisode episode) {
        Profile profile = null;
        try {
            profile = getProfile();
        } catch (IOException e) {
            profile = new Profile();
        }

        if (profile.getEpisodes() == null) {
            profile.setEpisodes(new ArrayList<>());
        }
        if (!profile.getEpisodes().contains(episode)) {
            profile.getEpisodes().add(episode);
        }
        write(profile);
    }

    @Override
    public boolean hasItem(ToDoEpisode episode) {
        Profile profile;
        try {
            profile = getProfile();
        } catch (IOException e) {
            return false;
        }

        if (profile.getEpisodes() != null) {
            return profile.getEpisodes().contains(episode);
        }
        return false;
    }

    @Override
    public void removeItem(ToDoEpisode episode) {
        try {
            Profile profile = getProfile();
            profile.getEpisodes().remove(episode);
            write(profile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setCurrentEpisode(ToDoEpisode episode) {
        Profile profile = null;
        try {
            profile = getProfile();
        } catch (IOException e) {
            profile = new Profile();
        }

        profile.setCurrentEpisode(episode);
        write(profile);
    }

    @Override
    public void setVolume(int volume) {
        Profile profile = null;
        try {
            profile = getProfile();
        } catch (IOException e) {
            profile = new Profile();
        }

        int validVolume = volume;
        if (validVolume < 0) {
            validVolume = 0;
        } else if (validVolume > 100) {
            validVolume = 100;
        }
        profile.setVolume(validVolume);
        write(profile);
    }

    private void write(Profile profile) {
        Yaml yaml = new Yaml();
        String data = yaml.dumpAs(profile, Tag.MAP, null);
        try (OutputStream output = new FileOutputStream(configFile)) {
            output.write(data.getBytes());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
