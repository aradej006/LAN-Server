package com.pw.lan.server.providers;

import com.pw.lan.server.domain.entities.Directory;
import com.pw.lan.server.domain.entities.User;
import com.pw.lan.server.domain.repositories.DirectoryRepository;
import com.pw.lan.server.domain.repositories.GroupRepository;
import com.pw.lan.server.domain.repositories.UserRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aradej on 2016-05-18.
 */
public class PermissionsProvider {

    private static PermissionsProvider instance;
    private DirectoryRepository directoryRepository;
    private UserRepository userRepository;

    private PermissionsProvider() {
        directoryRepository = DirectoryRepository.getInstance();
        userRepository = UserRepository.getInstance();
    }

    public static PermissionsProvider getInstance() {
        if (instance == null) instance = new PermissionsProvider();
        return instance;
    }


    public Map<String, String> setPermissions(Map<String, String> files, User user, String basePath) {
        Map<String, Directory> directories = new HashMap<>();
        userRepository.setGroups(user).getGroups()
                .forEach(g -> directoryRepository.setDirectories(g).getDirectories()
                        .forEach(d -> directories.put(d.getPath() + " " + g.getGroupName(), d)));
        files.entrySet().stream().filter(map -> map.getValue().contains("dir"))
                .forEach(e ->
                        directories.entrySet().stream().filter(dirs -> dirs.getKey().split(" ")[0].equals(basePath + e.getKey()))
                                .forEach(dir -> {
                                    e.setValue("dir false false");
                                    if (dir.getValue().isCanRead() && dir.getValue().isCanWrite()) {
                                        e.setValue("dir true true");
                                    } else if (dir.getValue().isCanRead() && e.getValue().equals("dir false false")) {
                                        e.setValue("dir true false");
                                    }
                                })
                );
        files.entrySet().removeIf( f -> f.getValue().equals("dir false false"));
        return files;
    }

}
