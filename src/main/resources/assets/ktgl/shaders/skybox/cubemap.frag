#version 150 core

in vec3 texCoord0;

out vec4 fragColor;

uniform vec4 ColorModulator;
uniform samplerCube Skybox;

void main() {
    fragColor = ColorModulator * texture(Skybox, texCoord0);
}
