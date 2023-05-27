package io.github.moonlightmaya.util;

import com.google.common.collect.ImmutableMap;
import io.github.moonlightmaya.AspectMod;
import net.fabricmc.loader.api.FabricLoader;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class IOUtils {

    /**
     * Navigate to the mod folder and create it if it doesn't exist.
     * In the future, functionality will be added for a customizable
     * mod folder location.
     */
    public static Path getOrCreateModFolder() {
        Path path = FabricLoader.getInstance().getGameDir().resolve(AspectMod.MODID);
        if (Files.notExists(path)) {
            try {
                AspectMod.LOGGER.info("Did not find mod folder at " + path + ". Creating...");
                Files.createDirectory(path);
                Files.createDirectory(path.resolve("aspects"));
                Files.createDirectory(path.resolve("guis"));
                AspectMod.LOGGER.info("Successfully created mod folder at " + path);
            } catch (IOException e) {
                AspectMod.LOGGER.error("Failed to create mod folder at " + path + ". Reason: ", e);
                return null;
            }
        } else {

            Path aspectsPath = path.resolve("aspects");
            if (Files.notExists(aspectsPath)) {
                try {
                    AspectMod.LOGGER.info("Did not find aspects folder at " + aspectsPath + ". Creating...");
                    Files.createDirectory(aspectsPath);
                    AspectMod.LOGGER.info("Successfully created aspects folder at " + aspectsPath);
                } catch (IOException e) {
                    AspectMod.LOGGER.error("Failed to create aspects folder at " + aspectsPath + ". Reason: ", e);
                    return null;
                }
            }

            Path guisPath = path.resolve("guis");
            if (Files.notExists(guisPath)) {
                try {
                    AspectMod.LOGGER.info("Did not find guis folder at " + guisPath + ". Creating...");
                    Files.createDirectory(guisPath);
                    AspectMod.LOGGER.info("Successfully created guis folder at " + guisPath);
                } catch (IOException e) {
                    AspectMod.LOGGER.error("Failed to create guis folder at " + guisPath + ". Reason: ", e);
                    return null;
                }
            }
        }
//        AspectMod.LOGGER.info("Located mod folder at " + path); //no need to spam this lol
        return path;
    }

    public static InputStream getAsset(String name) {
        return AspectMod.class.getResourceAsStream("/assets/" + AspectMod.MODID + "/" + name);
    }

    public static String trimPathStringToModFolder(Path p) {
        Path modFolder = getOrCreateModFolder();
        if (modFolder == null) return "<Error finding/creating mod folder>";
        return modFolder.relativize(p).toString();
    }

    /**
     * Doesn't recurse because im too dumb
     * If the path doesn't exist, then returns an empty list.
     */
    public static ArrayList<File> getByExtension(Path root, String extension) {
        File file = root.toFile();
        ArrayList<File> list = new ArrayList<>();
        if (file.exists() && file.isDirectory()) {
            File[] arr = file.listFiles((f,s) -> s.endsWith("."+extension));
            if (arr != null)
                Collections.addAll(list, arr);
        }
        return list;
    }

    public static ArrayList<File> getSubFolders(Path root) {
        File file = root.toFile();
        ArrayList<File> list = new ArrayList<>();
        if (file.exists() && file.isDirectory()) {
            File[] arr = file.listFiles(File::isDirectory);
            if (arr != null)
                Collections.addAll(list, arr);
        }
        return list;
    }

    //Compacting strategy:
    //first write an initial byte,
    //this byte contains information about the upcoming values .
    //every two bits of the initial byte carry information about an element
    //00 -> this element is zero
    //01 -> this element is a single integer that fits in a byte (signed )
    //10 -> this element is a float 0-1 that fits in a byte
    //11 -> this element needs full float precision
    //case 0 : the element is conveyed with no additional info
    //case 1 or 2 : only a single byte is needed for the element
    //case 3 , all 4 float bytes are needed.
    //most cases will likely fall into the first 3 categories , making this
    //an efficient encoding method. rarely, when all elements do not fit in one of the
    //first 3 categories , it costs an extra byte

    private static int getCase(float v) {
        if (MathUtils.epsilon(v)) return 0;
        if ((byte) v == v) return 1;
        if (v >= 0 && v < 1 && ((int) (v * 256) == v*256)) return 2;
        return 3;
    }

    private static void writeVectorElem(DataOutputStream dos, float v, int vCase) throws IOException {
        switch (vCase) {
            case 0 -> {}
            case 1 -> dos.writeByte((byte) v);
            case 2 -> dos.writeByte((int) (v*256));
            case 3 -> dos.writeFloat(v);
            default -> throw new IllegalArgumentException("Invalid case arg to Vector write: " + vCase);
        }
    }

    private static float readVectorElem(DataInputStream dis, int vCase) throws IOException {
        return switch (vCase) {
            case 0 -> 0;
            case 1 -> (byte) (dis.read());
            case 2 -> dis.read() / 256f;
            case 3 -> dis.readFloat();
            default -> throw new IllegalArgumentException("Invalid case arg to Vector read: " + vCase);
        };
    }

    public static void writeVector3f(DataOutputStream dos, Vector3f vec) throws IOException {
        int xCase = getCase(vec.x);
        int yCase = getCase(vec.y);
        int zCase = getCase(vec.z);
        dos.writeByte(xCase | (yCase << 2) | (zCase << 4));
        writeVectorElem(dos, vec.x, xCase);
        writeVectorElem(dos, vec.y, yCase);
        writeVectorElem(dos, vec.z, zCase);
    }

    public static Vector3f readVector3f(DataInputStream dis) throws IOException {
        int initial = dis.read();
        int xCase = initial & 0b11;
        int yCase = (initial & 0b1100) >> 2;
        int zCase = (initial & 0b110000) >> 4;
        float x = readVectorElem(dis, xCase);
        float y = readVectorElem(dis, yCase);
        float z = readVectorElem(dis, zCase);
        return new Vector3f(x, y, z);
    }

    public static void writeVector4f(DataOutputStream dos, Vector4f vec) throws IOException {
        int xCase = getCase(vec.x);
        int yCase = getCase(vec.y);
        int zCase = getCase(vec.z);
        int wCase = getCase(vec.w);
        dos.writeByte(xCase | (yCase << 2) | (zCase << 4) | (wCase << 6));
        writeVectorElem(dos, vec.x, xCase);
        writeVectorElem(dos, vec.y, yCase);
        writeVectorElem(dos, vec.z, zCase);
        writeVectorElem(dos, vec.w, wCase);
    }

    public static Vector4f readVector4f(DataInputStream dis) throws IOException {
        int initial = dis.read();
        int xCase = initial & 0b11;
        int yCase = (initial & 0b1100) >> 2;
        int zCase = (initial & 0b110000) >> 4;
        int wCase = (initial & 0b11000000) >> 6;
        float x = readVectorElem(dis, xCase);
        float y = readVectorElem(dis, yCase);
        float z = readVectorElem(dis, zCase);
        float w = readVectorElem(dis, wCase);
        return new Vector4f(x, y, z, w);
    }

    //VarInt code from https://wiki.vg/VarInt_And_VarLong
    public static void writeVarInt(DataOutputStream dos, int value) throws IOException {
        while (true) {
            if ((value & ~0x7F) == 0) {
                dos.writeByte(value);
                return;
            }
            dos.writeByte((value & 0x7F) | 0x80);
            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            value >>>= 7;
        }
    }
    public static int readVarInt(DataInputStream dis) throws IOException {
        int value = 0;
        int position = 0;
        byte currentByte;

        while (true) {
            currentByte = dis.readByte();
            value |= (currentByte & 0x7F) << position;
            if ((currentByte & 0x80) == 0) break;
            position += 7;
            if (position >= 32) throw new IOException("VarInt is too big");
        }

        return value;
    }

    public static class AspectIOException extends RuntimeException {
        public AspectIOException(IOException wrapped) {
            super(wrapped);
        }
    }

}
