package com.chrisstar123.chestsorter.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import com.chrisstar123.chestsorter.ChestSorter;

import de.jeffclan.utils.Utils;

public class JeffChestSortHelper {
    // Saves default category files, when enabled in the config
    public static void saveDefaultCategories() {
        createDirectories();

        // Isn't there a smarter way to find all the 9** files in the .jar?
        String[] defaultCategories = { "900-weapons", "905-common-tools", "907-other-tools", "909-food",
                "910-valuables", "920-armor-and-arrows", "930-brewing", "950-redstone", "960-wood", "970-stone",
                "980-plants", "981-corals", "_ReadMe - Category files" };

        File catDir = new File(
                ChestSorter.cs.getDataFolder().getAbsolutePath() + File.separator + "categories" + File.separator);

        if (catDir != null && catDir.isDirectory() && catDir.listFiles().length > 0) {
            // Delete all files starting with 9..
            for (File file : catDir.listFiles(new FilenameFilter() {
                public boolean accept(File directory, String fileName) {
                    if (!fileName.endsWith(".txt")) {
                        return false;
                    }
                    if (fileName.matches("(?i)9\\d\\d.*\\.txt$")) // Category between 900 and 999-... are
                                                                  // default
                                                                  // categories
                    {
                        return true;
                    }
                    return false;
                }
            })) {

                boolean delete = true;

                for (String name : defaultCategories) {
                    name = name + ".txt";
                    if (name.equalsIgnoreCase(file.getName())) {
                        delete = false;
                        break;
                    }
                }
                if (delete) {
                    file.delete();
                    // ChestSorter.cs.getLogger().warning("Deleting deprecated default category file
                    // " + file.getName());
                }

            }
        }

        for (String category : defaultCategories) {

            FileOutputStream fopDefault = null;
            File fileDefault;

            try {
                InputStream in = ChestSorter.cs.getClass()
                        .getResourceAsStream("/categories/" + category + ".default.txt");

                fileDefault = new File(ChestSorter.cs.getDataFolder().getAbsolutePath() + File.separator + "categories"
                        + File.separator + category + ".txt");
                fopDefault = new FileOutputStream(fileDefault);

                // overwrites existing files, on purpose.
                fileDefault.createNewFile();

                // get the content in bytes
                byte[] contentInBytes = Utils.getBytes(in);

                fopDefault.write(contentInBytes);
                fopDefault.flush();
                fopDefault.close();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fopDefault != null) {
                        fopDefault.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void createDirectories() {
        // Create a categories folder that contains text files. ChestSort includes
        // default category files,
        // but you can also create your own
        File categoriesFolder = new File(ChestSorter.cs.getDataFolder().getPath() + File.separator + "categories");
        if (!categoriesFolder.getAbsoluteFile().exists()) {
            categoriesFolder.mkdir();
        }
    }
}
