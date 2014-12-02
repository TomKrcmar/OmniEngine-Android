
#version 150

uniform mat4 mvpMatrix;

uniform sampler2D diffuseSampler;

in vec2 fragTexCoord;
in vec4 fragColor;

out vec4 outFragColor;

void main()
{
	vec4 diffuseSample = texture2D(diffuseSampler, fragTexCoord);
	
	outFragColor = diffuseSample * fragColor;
}