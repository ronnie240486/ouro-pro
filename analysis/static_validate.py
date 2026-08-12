from pathlib import Path
import re
import xml.etree.ElementTree as ET

root = Path('/home/ubuntu/ouro-pro')
ET.parse(root / 'app/src/main/AndroidManifest.xml')
voice = (root / 'app/src/main/java/com/ouropro/player/improvements/VoiceCommand.java').read_text(encoding='utf-8')
live = (root / 'app/src/main/java/com/ouropro/player/activities/LiveActivity.java').read_text(encoding='utf-8')
controller = (root / 'app/src/main/java/com/ouropro/player/improvements/VoiceCommandController.java').read_text(encoding='utf-8')
assert 'android.permission.RECORD_AUDIO' in (root / 'app/src/main/AndroidManifest.xml').read_text(encoding='utf-8')
assert 'android.speech.RecognitionService' in (root / 'app/src/main/AndroidManifest.xml').read_text(encoding='utf-8')
for action in ['OPEN_CHANNEL', 'OPEN_LIVE', 'OPEN_MOVIES', 'OPEN_SERIES', 'OPEN_SETTINGS', 'SEARCH_CHANNEL', 'NEXT_CHANNEL', 'PREVIOUS_CHANNEL', 'PLAY', 'PAUSE', 'UNKNOWN']:
    assert re.search(r'\b' + action + r'\b', voice)
    assert re.search(r'VoiceCommand\.Action\.' + action + r'|case ' + action + r'\b', live) or action == 'UNKNOWN'
assert 'SpeechRecognizer' in controller
assert 'playSelectedChannel(channel)' in live
assert 'showChannelLockDlgFragment(channel, index, 0)' in live
assert 'voiceCommandController.stop()' in live
assert 'Level.BODY' not in (root / 'app/src/main/java/com/ouropro/player/remote/RetroClass.java').read_text(encoding='utf-8')
print('static-validation: ok')
