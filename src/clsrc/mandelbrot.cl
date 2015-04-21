typedef struct Complex {   
	 float r;   
	 float i;   
 } Complex; 
 
 #define LIMIT 4

 //intParams[0,1,2] = iterations, resolutionx, y 
 //floatParams[0,1,2,3] = position of panning x,y, zoom factor, aspectRatio
__kernel void mandelbrot(__global const int *intParams, __global const float *floatParams, __global int *output) 
{ 
	int stopped = -1; 
	 
	Complex C; 
	Complex sum; 
	Complex previous; 
	 
	//coordinates in -1 to +1 
	const float screenx = (( ((float)get_global_id(0)) / ((float)(intParams[1]-1))) - 0.5f) * 2.0f; 
	const float screeny = (( ((float)get_global_id(1)) / ((float)(intParams[2]-1))) - 0.5f) * 2.0f; 
	 
	const uint imgid = get_global_id(1) * intParams[1] + get_global_id(0); 
	 
	C.r = floatParams[0] + floatParams[2] * screenx; //  x 
	C.i = floatParams[1] + (floatParams[2] * screeny) * floatParams[3]; //  y 
	 
	sum.r = 0; //  x 
	sum.i = 0; //  y 
	 
	previous.r = 0; 
	previous.i = 0; 
	 
	for (int i = 0; i < intParams[0]; i++) 
	{ 
		sum.r = (previous.r * previous.r - previous.i * previous.i) + C.r; 
		sum.i = (previous.i * previous.r * 2) + C.i; 
		 
		if (sum.r * sum.r + sum.i * sum.i > LIMIT) 
		{ 
			stopped = i; 
			break; 
		} 
		 
		previous.r = sum.r; 
		previous.i = sum.i; 
	} 
	 
	if (stopped != -1) 
	{ 
		float stoppedPercentage = ((float)stopped) / ((float)((intParams[0])-1)); 
		output[imgid] = ((int)(stoppedPercentage*155 + 100)) ;
	} 
	else 
	{ 
		output[imgid] = 0;
	} 
}