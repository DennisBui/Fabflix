import sys

f = open("logFile9.txt")

data = f.read()
tj = 0
ts = 0
total_tj = 0
total_ts = 0

track = 0
i = 0

for time in data.split("\n"):
	print("hi")
    currElement = time.split(" ")
    
    if len(currElement) == 2:
        if track == 0:
            ts += int(float(currElement[0]))
            total_ts += 1
            track = 1
            
        elif track == 1:
            tj += int(float(currElement[1]))
            total_tj += 1
            track = 1

    currElement.clear()
    
avg_tj = tj/total_tj
avg_ts = ts/total_ts

print ("tj avg ", avg_tj)
print ("ts avg ", avg_ts)

