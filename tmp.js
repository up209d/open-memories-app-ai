const fs = require('fs');
const path = require('path');
const crypto = require('crypto');

// Target is your F: drive
const sdPath = 'F:\\io_attack'; 

if (!fs.existsSync(sdPath)) {
    try {
        fs.mkdirSync(sdPath, { recursive: true });
    } catch (err) {
        console.error("Could not create directory on F: drive. Is the SD card write-protected?", err);
        process.exit(1);
    }
}

console.log(`Targeting SD Card at: ${sdPath}`);
console.log('Generating 100,000 UUID files. This will create a massive I/O load...');

const totalFiles = 10000;

for (let i = 1; i <= totalFiles; i++) {
    try {
        const uuid = crypto.randomUUID();
        const filePath = path.join(sdPath, `${uuid}.txt`);
        
        // Writing unique data to prevent file system deduplication
        fs.writeFileSync(filePath, `payload_id_${uuid}_sequence_${i}`);
        
        if (i % 5000 === 0) {
            const progress = ((i / totalFiles) * 100).toFixed(0);
            console.log(`Progress: ${progress}% (${i} files created)`);
        }
    } catch (err) {
        // If the card is slow, it might occasionally throw an EBUSY or ENOSPC
        if (err.code === 'ENOSPC') {
            console.error('Out of space on SD card! Stopping here.');
            break;
        }
    }
}

console.log('\nDone! Safely Eject the F: drive before putting it in the camera.');
