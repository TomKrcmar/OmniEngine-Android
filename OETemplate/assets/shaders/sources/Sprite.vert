
#version 150

uniform mat4 mvpMatrix;

uniform sampler2D diffuseSampler;

in vec3 vertPosition;
in vec2 vertTexCoord;
in vec4 vertColor;

out vec2 fragTexCoord;
out vec4 fragColor;

void main()
{
	fragTexCoord = vertTexCoord;
	fragColor = vertColor;
	gl_Position = mvpMatrix * vec4(vertPosition, 1.0);
}