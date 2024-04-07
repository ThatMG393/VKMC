package com.thatmg393.vkmc.vulkan.shader.layout;

public final class Info {
    public static Info createUniformInfo(String name, String type, int count) {
        return switch (type) {
            case "matrix4x4" -> new Info(name, "mat4", 4, 16);
            case "int" -> new Info(name, "int", 1, 1);
            case "float" -> switch (count) {
                case 1 -> new Info(name, "float", 1, 1);
                case 2 -> new Info(name, "vec2", 2, 2);
                case 3 -> new Info(name, "vec3", 4, 3);
                case 4 -> new Info(name, " vec4", 4, 4);

                default -> throw new RuntimeException("Invalid count! Only accepts values 1-4!");
            };
            default -> throw new RuntimeException("not admitted type..");
        };
    }

    private final String name;
    public String getName() {
        return name;
    }

    private final String type;
    public String getType() {
        return type;
    }

    private final int align;
    public int getAlign() {
        return align;
    }

    private final int size;

    public Info(
        String name,
        String type,
        int align,
        int size
    ) {
        this.name = name;
        this.type = type;
        this.align = align;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    private int offset;

    public int getOffset() {
        return offset;
    }

    public int getSizeBytes() {
        return 4 * this.size;
    }

    public long computeAlignOffset(int builderOffset) {
        return offset = builderOffset + ((align - (builderOffset % align)) % align);
    }
}
