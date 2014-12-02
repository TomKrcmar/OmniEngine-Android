
uniform lowp sampler2D diffuseSampler;

varying lowp vec2 fragTexCoord;
varying lowp vec4 fragColor;

void main()
{
	gl_FragColor = texture2D(diffuseSampler, fragTexCoord) * fragColor;
}