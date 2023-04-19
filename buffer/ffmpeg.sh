--------------------------------------------------------------------------------
cd /root/temp/to-delete/ffmpeg/open_brush_bm

####################
# IMAGE RESIZE
####################
mkdir landscape
for fullfile in *.jpg; do
  filename=$(basename -- "$fullfile"); extension="${filename##*.}"; f="${filename%.*}"
  convert ${fullfile} -resize 1920x1080 -gravity center -background "rgb(0,0,0)" -extent 1920x1080 landscape/${fullfile}
done

####################
# ROTATE, 0 = 90° counterclockwise and vertical flip (default), 1 = 90° clockwise, 2 = 90° counterclockwise, 3 = 90° clockwise and vertical flip
# https://stackoverflow.com/questions/3937387/rotating-videos-with-ffmpeg
####################
mkdir rotated
ffmpeg -i ${f}.mp4 -vf "transpose=1" rotated/${f}.mp4

####################
# ffmpeg concat
####################
ffmpeg -f concat -safe 0 -i concat.txt -vf "fps=30" ../belen.mp4

####################
# ffmpeg-concat
# --no-cleanup-frames --temp-dir=/tmp/ffmpeg-concat \
####################
time xvfb-run -s "-ac -screen 0 1920x1080x24" \
ffmpeg-concat -d 250 \
  xxx.mp4 \
  xxx.mp4 \
  -o ../final.mp4 
  xxx.mp4 \
  xxx.mp4 \

####################
# ADJUST VIDEO SPEED; > 1 = SLOW DOWN; < 1 = SPEED UP
####################
mkdir -p speed_mod
for fullfile in *.mp4; do
  # ffmpeg -nostdin -y -itsscale 0.25 -i ${fullfile} -c copy speed_mod/${fullfile}
  ffmpeg -nostdin -y -i ${fullfile} -filter:v "setpts=PTS/6,fps=30" speed_mod/${fullfile}
done
cd speed_mod

####################
# PARTIAL SPEED MOD
####################
filename=$(basename -- "$f"); extension="${filename##*.}"; f="${filename%.*}"
mkdir -p speed_mod
LENGTH=`ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 ${f}.mp4`
PERCENTAGE=0.8
FIRST=`echo "$LENGTH * $PERCENTAGE" | bc`
ffmpeg -nostdin -y -i ${f}.mp4 -c copy -an speed_mod/${f}_na.mp4
cd speed_mod
ffmpeg -nostdin -y -i ${f}_na.mp4 \
  -filter_complex \
     "[0:v]trim=0:$FIRST,setpts=0.5*(PTS-STARTPTS)[v1]; \
        [0:v]trim=$FIRST,setpts=2.0*(PTS-STARTPTS)[v2]; \
      [v1][v2]concat=n=2:v=1:" \
  -preset superfast -profile:v baseline ${f}.mp4

####################
# ADD SUBTITLES
####################
ffmpeg -i belen.mp4 -vf subtitles=subtitles.ass belen_st.mp4

####################
# CLIPPING
####################
filename=$(basename -- "$fullfile"); extension="${filename##*.}"; f="${filename%.*}"
ffmpeg -r 30/1 -i ${f}.png -c:v libx264 -vf fps=25 -pix_fmt yuv420p ${f}.mp4

####################
# IMG TO MP4
####################
mkdir mp4
for fullfile in *.jpg; do
  filename=$(basename -- "$fullfile"); extension="${filename##*.}"; f="${filename%.*}"
  ffmpeg -y -loop 1 -i ${fullfile} -framerate 30 -c:v libx264 -t 5 -pix_fmt yuv420p mp4/${f}.mp4
done

####################
# CLIPPING
####################
  filename=$(basename -- "$fullfile"); extension="${filename##*.}"; f="${filename%.*}"
ffmpeg -i "${f}".mp4 -ss 20 -to 50 -c:v libx264 -c:a aac -strict experimental -b:a 128k ../clipped/"${f}".mp4

####################
# CLIPPING
# https://ostechnix.com/zoom-in-and-zoom-out-videos-using-ffmpeg/
####################
filename=$(basename -- "$f"); extension="${filename##*.}"; f="${filename%.*}"
ZOOM=1
FACTOR=1
ffmpeg -y -i "${f}".mp4 \
  -vf "zoompan=z='min(max(zoom,pzoom)+${ZOOM},${ZOOM})':d=1:x='iw/$FACTOR-(iw/zoom/$FACTOR)':y='ih/$FACTOR-(ih/zoom/$FACTOR)':fps=30'" \
  "${file_name}"_zoom.mp4

####################
# BREAK VIDEO INTO 10 MINUTE CHUNKS
# https://unix.stackexchange.com/questions/1670/how-can-i-use-ffmpeg-to-split-mpeg-video-into-10-minute-chunks
####################
filename=$(basename -- "$f"); extension="${filename##*.}"; f="${filename%.*}"
ffmpeg -i ${f}.mp4 -c copy -map 0 -segment_time 00:10:00 -f segment -reset_timestamps 1 ${f}_%03d.mp4

####################
# ADD AUDIO
####################
filename=$(basename -- "$f"); extension="${filename##*.}"; f="${filename%.*}"
ffmpeg -y -i ${f}.mp4 -i audio.mp3 -map 0 -map 1:a -c:v copy ${f}_wa.mp4

####################
# CONVERT MP4 TO GIF
####################
filename=$(basename -- "$f"); extension="${filename##*.}"; f="${filename%.*}"
ffmpeg -i "${f}".mp4 \
  -vf "fps=30,scale=1920:-1:flags=lanczos,split[s0][s1];[s0]palettegen[p];[s1][p]paletteuse" \
  -loop 0 "${f}".gif

####################
# REMOVE AUDIO
####################
for fullfile in *.mp4; do
  filename=$(basename -- "$fullfile"); extension="${filename##*.}"; f="${filename%.*}"
  ffmpeg -i ${f}.mp4 -c copy -an ../no_audio/${f}.mp4
done

####################
# REVERSE VIDEO
####################
filename=$(basename -- "$fullfile"); extension="${filename##*.}"; f="${filename%.*}"
ffmpeg -i ${f}.mp4 -vf reverse ${f}_reversed.mp4

####################
# EXTRPACT AUDIO FROM WEBM
####################
filename=$(basename -- "$fullfile"); extension="${filename##*.}"; f="${filename%.*}"
ffmpeg -i "${f}".webm -vn -ab 128k -ar 44100 -y "${f}".mp3;

####################
# SHOW LENGTH
####################
filename=$(basename -- "$fullfile"); extension="${filename##*.}"; f="${filename%.*}"
ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 ${f}.mp4
ffprobe -i ${f}.mp4 -show_entries format=duration -v quiet -of csv="p=0"
ffprobe -loglevel error -show_streams ${f}.mp4 | grep duration | cut -f2 -d=
ffprobe -v error -select_streams v:0 -show_entries stream=duration -of default=noprint_wrappers=1:nokey=1 ${f}.mp4

####################
# SHOW RESOLUTION, FPS, LENGTH
####################
for fullfile in *.mp4; do
  filename=$(basename -- "$fullfile"); extension="${filename##*.}"; f="${filename%.*}"
  RESO=`ffprobe -v error -select_streams v:0 -show_entries stream=width,height -of csv=s=x:p=0 ${filename}`
  if [ $RESO != "1920x1080" ]; then
    RESO="\033[0;31m${RESO}\033[0m"
  fi
  FPS=`ffprobe -v 0 -of csv=p=0 -select_streams v:0 -show_entries stream=r_frame_rate ${filename}`
  FPS=`printf '%-12s' "${FPS}"`
  LENGTH=`ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 ${filename}`
  LENGTH=`printf '%-12s' "${LENGTH}"`
  printf "${RESO} ${FPS} ${LENGTH} ${f} \n"
done

  # STAGE NON 1920x1080 FOR RE SCALING
  for fullfile in *.mp4; do
    filename=$(basename -- "$fullfile"); extension="${filename##*.}"; f="${filename%.*}"
    RESO=`ffprobe -v error -select_streams v:0 -show_entries stream=width,height -of csv=s=x:p=0 ${filename}`
    if [ $RESO != "1920x1080" ]; then
      mkdir -p to_rescale
      mv ${filename} to_rescale/${filename}
    fi
  done


####################
# SHOW FILES WITH AUDIO STREAM
####################
for fullfile in *.mp4; do
  filename=$(basename -- "$fullfile"); extension="${filename##*.}"; f="${filename%.*}"
  AUDIO=`ffprobe -show_streams -select_streams a ${f}.mp4 2>&1 | grep Audio`
  if [ -n "$AUDIO" ]; then
    echo ${f} ${AUDIO}
  fi
done

############################################################
# PORTRAIT TO LANDSCAPE TO SCALED 1920x1080
############################################################


# LOOP PORTRAIT TO LANDSCAPE
mkdir -p landscape;
for fullfile in *.mp4; do
  filename=$(basename -- "$fullfile"); extension="${filename##*.}"; f="${filename%.*}"
  time ffmpeg -i ${f}.mp4 -lavfi '[0:v]scale=ih*16/9:-1,boxblur=luma_radius=min(h\,w)/20:luma_power=1:chroma_radius=min(cw\,ch)/20:chroma_power=1[bg];[bg][0:v]overlay=(W-w)/2:(H-h)/2,crop=h=iw*9/16' landscape/${f}.mp4
done

# LANDSCAPE -> SCALED TO 1920x1080
cd landscape; mkdir scaled
for fullfile in *.mp4; do
  filename=$(basename -- "$fullfile"); extension="${filename##*.}"; f="${filename%.*}"
  time ffmpeg -i ${f}.mp4 -vf scale=1920:1080 scaled/${f}.mp4
done

####################
# FPS
####################
mkdir FPS
for fullfile in *.mp4; do
  filename=$(basename -- "$fullfile"); extension="${filename##*.}"; f="${filename%.*}"
  time ffmpeg -i ${f}.mp4 -filter:v fps=fps=30 FPS/${f}.mp4
done

####################
# PREFIX ASCENDING FILENAMES WITH NUMBER
# ls to list files sorted by filename without the extension
# https://superuser.com/questions/747961/ls-to-list-files-sorted-by-filename-without-the-extension
####################
n=1;
for f in `ls | sort -k 1,1 -t . | tail -n +1`
do
  f_name=$(printf "%03d" $n)_$f
  mv $f $f_name
  echo $((n++)) > /dev/null
done

####################
# REMOVE SPACES 
####################
for f in *\ *; do mv "$f" "${f// /_}"; done
rename 's/_\-_Trim//' *
# does this work?
for f in *\ *; do mv "$f" "${f/_\-_Trim//}"; done

rename 's/\.jpg//' *

# SHOW FILENAMES ONLY
find ./  -printf "%f\n"
