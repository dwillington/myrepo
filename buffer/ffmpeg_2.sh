####################
# PORTRAIT TO LANDSCAPE
####################
# DIDN'T WORK
mkdir landscape
ffmpeg -i ${f}.mp4 -lavfi '[0:v]scale=ih*16/9:-1,boxblur=luma_radius=min(h\,w)/20:luma_power=1:chroma_radius=min(cw\,ch)/20:chroma_power=1[bg];[bg][0:v]overlay=(W-w)/2:(H-h)/2,crop=h=iw*9/16' -vb 800K landscape/${f}.mp4


ffmpeg -y -i ${f}.mp4 -lavfi "[0:v]scale=1920*2:1080*2,boxblur=luma_radius=min(h\,w)/20:luma_power=1:chroma_radius=min(cw\,ch)/20:chroma_power=1[bg];[0:v]scale=-1:1080[ov];[bg][ov]overlay=(W-w)/2:(H-h)/2,crop=w=1920:h=1080" landscape/${f}.mp4



ffmpeg -i ${f}.mp4 -vf  'split[original][copy];[copy]scale=-1:ih*(16/9)*(16/9),crop=w=ih*9/16,gblur=sigma=20[blurred];[blurred][original]overlay=(main_w-overlay_w)/2:(main_h-overlay_h)/2' landscape/${f}.mp4

  # if [ $RESO = "1920x1080x" ]; then
    # mv ${f}.mp4 1920x1080x/${f}.mp4
  # else
    # echo "skipping ${f}"
  # fi


############################################################
# PORTRAIT TO LANDSCAPE TO SCALED 1920x1080
############################################################
# SINGLE FILE
mkdir landscape
time ffmpeg -i ${f}.mp4 -lavfi '[0:v]scale=ih*16/9:-1,boxblur=luma_radius=min(h\,w)/20:luma_power=1:chroma_radius=min(cw\,ch)/20:chroma_power=1[bg];[bg][0:v]overlay=(W-w)/2:(H-h)/2,crop=h=iw*9/16' -vf scale=1920:1080 landscape/${f}.mp4
mkdir scaled
time ffmpeg -i landscape/${f}.mp4 -filter:v fps=fps=30 landscape/scaled/${f}.mp4


####################
# PARTIAL SPEED MOD
####################
filename=$(basename -- "$f"); extension="${filename##*.}"; f="${filename%.*}"
mkdir -p speed_mod
LENGTH=`ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 ${f}.mp4`
PERCENTAGE=0.8
FIRST=`echo "$LENGTH * $PERCENTAGE" | bc`

# METHOD 1
ffmpeg -nostdin -y -i ${f}.mp4 -t  $FIRST -c copy -an speed_mod/${f}_1.mp4
ffmpeg -nostdin -y -i ${f}.mp4 -ss $FIRST -c copy -an speed_mod/${f}_2.mp4
cd speed_mod
ffmpeg -nostdin -y -i ${f}_1.mp4 -filter:v "setpts=PTS/2.00,fps=30" ${f}_1f.mp4
ffmpeg -nostdin -y -i ${f}_2.mp4 -filter:v "setpts=PTS/0.25,fps=30" ${f}_2s.mp4
time xvfb-run -s "-ac -screen 0 1920x1080x24" \
ffmpeg-concat -d 50 --no-cleanup-frames --temp-dir=/tmp/ffmpeg-concat \
  ${f}_1f.mp4 \
  ${f}_2s.mp4 \
  -o ${f}.mp4 

# METHOD 2
echo "" > concat.txt
echo "file ${f}_1f.mp4" >> concat.txt
echo "file ${f}_2s.mp4" >> concat.txt
ffmpeg -nostdin -y -f concat -safe 0 -i concat.txt -vf "fps=30" ${f}.mp4 
