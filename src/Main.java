import java.nio.file.Path;
import java.util.*;

public class Main {
    public static void main (final String[] args) {
        List<String> paths = new ArrayList<>();
        final Map<String, Tree.Setting> settingsMap = new HashMap<>() {{
            put("a", Tree.Setting.SHOW_ALL_FILES);
            put("u", Tree.Setting.SHOW_USER_NAMES);
            put("g", Tree.Setting.SHOW_GROUP_NAMES);
            put("s", Tree.Setting.SHOW_FILE_SIZE);
            put("p", Tree.Setting.SHOW_FILE_PERMISSIONS);
        }};

        Set<Tree.Setting> settings = new TreeSet<>();
        boolean expectMaxDepthNextArgument = false;
        int maxDepth = -1;

        for (final String arg : args) {
            if (expectMaxDepthNextArgument) {
                try {
                    maxDepth = Integer.parseInt(arg);

                    if (maxDepth <= 0)
                        throw new NumberFormatException();
                } catch (final NumberFormatException ignored) {
                    System.out.println("tree: Invalid level, must be greater than 0.");
                    System.exit(1);
                }

                expectMaxDepthNextArgument = false;
                continue;
            }

            if (arg.startsWith("-")) {
                for (final Map.Entry<String, Tree.Setting> setting : settingsMap.entrySet())
                    if (arg.contains(setting.getKey()))
                        settings.add(setting.getValue());

                if (arg.contains("L"))
                    expectMaxDepthNextArgument = true;
            }

            else
                paths.add(arg);
        }

        Tree.tree(paths.stream().map(Path::of).toArray(Path[]::new), maxDepth, settings.stream().toList());
    }
}
