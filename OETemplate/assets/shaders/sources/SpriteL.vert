
uniform mat4 mvpMatrix;

uniform sampler2D diffuseSampler;

attribute vec3 vertPosition;
attribute vec2 vertTexCoord;
attribute vec4 vertColor;

varying vec2 fragTexCoord;
varying vec4 fragColor;

void main()
{
	fragTexCoord = vertTexCoord;
	fragColor = vertColor;
	gl_Position = mvpMatrix * vec4(vertPosition, 1.0);
}