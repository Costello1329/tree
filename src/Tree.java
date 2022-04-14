import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.*;
import java.util.*;

public class Tree {
    public enum Setting {
        SHOW_ALL_FILES,
        SHOW_USER_NAMES,
        SHOW_GROUP_NAMES,
        SHOW_FILE_SIZE,
        SHOW_FILE_PERMISSIONS
    }

    public static void tree (Path[] folders, final int maxDepth, final List<Setting> settings) {
        final int[] counters = new int[2];

        for (final Path folder : folders) {
            if (Files.exists(folder) && Files.isDirectory(folder)) {
                System.out.println(folder);

                try {
                    listRecursive(folder, 0, new ArrayList<>(), counters, maxDepth, settings);
                } catch (final IOException ignored) {
                    System.out.println(folder + " [error opening dir]");
                }
            }

            else
                System.out.println(folder + " [error opening dir]");
        }

        System.out.printf(
            "\n%d %s, %d %s\n",
            counters[0],
            counters[0] == 1 ? "directory" : "directories",
            counters[1],
            counters[1] == 1 ? "file" : "files"
        );
    }

    private static void listRecursive (
        final Path folder,
        final int depth,
        final ArrayList<Integer> openedFolders,
        final int[] counters,
        final int maxDepth,
        final List<Setting> settings
    ) throws IOException {
        List<Path> files = Files.list(folder)
            .filter((final Path file) -> {
                if (settings.contains(Setting.SHOW_ALL_FILES))
                    return true;

                try {
                    return !Files.isHidden(file);
                } catch (final IOException ignored) {
                    return false;
                }
            })
            .sorted(Path::compareTo)
            .toList();

        for (int i = 0; i < files.size(); i ++) {
            final Path file = files.get(i);

            for (int j = 0; j < depth; j ++)
                System.out.print(openedFolders.contains(j) ? VERTICAL_LINE : EMPTY);

            System.out.print((i == files.size() - 1 ? CORNER_LINE : T_LINE));

            if (
                settings.contains(Setting.SHOW_FILE_PERMISSIONS) ||
                settings.contains(Setting.SHOW_USER_NAMES) ||
                settings.contains(Setting.SHOW_GROUP_NAMES) ||
                settings.contains(Setting.SHOW_FILE_SIZE)
            ) {
                List<String> fileInfo = new ArrayList<>();

                if (settings.contains(Setting.SHOW_FILE_PERMISSIONS))
                    fileInfo.add(getFilePermissions(file));

                if (settings.contains(Setting.SHOW_USER_NAMES))
                    fileInfo.add(getUserName(file));

                if (settings.contains(Setting.SHOW_GROUP_NAMES))
                    fileInfo.add(getGroupName(file));

                if (settings.contains(Setting.SHOW_FILE_SIZE))
                    fileInfo.add(getFileSize(file));

                System.out.printf("[%s]  ", String.join(" ", fileInfo));
            }

            System.out.print(file.getName(file.getNameCount() - 1));

            if (Files.isSymbolicLink(file)) {
                System.out.print(" -> ");
                final Path realPath = file.toRealPath();

                System.out.print(
                    file.getParent() != null && realPath.getParent() != null &&
                        Files.isSameFile(file.getParent(), realPath.getParent()) ?
                    realPath.getName(realPath.getNameCount() - 1) :
                    realPath
                );

                System.out.println();
            }

            else if (Files.isDirectory(file) && (maxDepth < 0 || depth + 1 < maxDepth)) {
                System.out.println();

                if (i != files.size() - 1)
                    openedFolders.add(depth);

                listRecursive(file, depth + 1, openedFolders, counters, maxDepth, settings);

                if (i != files.size() - 1)
                    openedFolders.remove(openedFolders.size() - 1);
            }

            else
                System.out.println();

            ++ counters[Files.isDirectory(file) ? 0 : 1];
        }
    }

    private static PosixFileAttributes getFileAttributes (final Path file) {
        try {
            return Files.readAttributes(file, PosixFileAttributes.class);
        } catch (final IOException ignored) {
            return null;
        }
    }

    private static String getFilePermissions (final Path file) {
        final char fileType;

        if (Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS))
            fileType = '-';

        else if (Files.isDirectory(file, LinkOption.NOFOLLOW_LINKS))
            fileType = 'd';

        else if (Files.isSymbolicLink(file))
            fileType = 'l';

        else
            fileType = '?';

        final char[] permissionsChars = new char[9];
        Arrays.fill(permissionsChars, '-');

        final Set<PosixFilePermission> permissions = Objects.requireNonNull(getFileAttributes(file)).permissions();

        final List<PosixFilePermission> permissionsOrder = new ArrayList<>() {{
            add(PosixFilePermission.OWNER_READ);
            add(PosixFilePermission.OWNER_WRITE);
            add(PosixFilePermission.OWNER_EXECUTE);
            add(PosixFilePermission.GROUP_READ);
            add(PosixFilePermission.GROUP_WRITE);
            add(PosixFilePermission.GROUP_EXECUTE);
            add(PosixFilePermission.OTHERS_READ);
            add(PosixFilePermission.OTHERS_WRITE);
            add(PosixFilePermission.OTHERS_EXECUTE);
        }};

        final Map<PosixFilePermission, Character> permissionsPresentation = new HashMap<>() {{
            put(PosixFilePermission.OWNER_READ, 'r');
            put(PosixFilePermission.GROUP_READ, 'r');
            put(PosixFilePermission.OTHERS_READ, 'r');
            put(PosixFilePermission.OWNER_WRITE, 'w');
            put(PosixFilePermission.GROUP_WRITE, 'w');
            put(PosixFilePermission.OTHERS_WRITE, 'w');
            put(PosixFilePermission.OWNER_EXECUTE, 'x');
            put(PosixFilePermission.GROUP_EXECUTE, 'x');
            put(PosixFilePermission.OTHERS_EXECUTE, 'x');
        }};

        for (final PosixFilePermission permission : permissions)
            permissionsChars[permissionsOrder.indexOf(permission)] = permissionsPresentation.get(permission);

        return String.format("%c%s", fileType, new String(permissionsChars));
    }

    private static String getUserName (final Path file) {
        return Objects.requireNonNull(getFileAttributes(file)).owner().getName();
    }

    private static String getGroupName (final Path file) {
        return Objects.requireNonNull(getFileAttributes(file)).group().getName();
    }

    private static String getFileSize (final Path file) {
        return String.valueOf(file.toFile().length());
    }

    private final static String EMPTY = "    ";
    private final static String VERTICAL_LINE = "│   ";
    private final static String CORNER_LINE = "└── ";
    private final static String T_LINE = "├── ";
}
