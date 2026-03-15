import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-import',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './import.component.html'
})
export class ImportComponent {
  selectedFile: string | null = null;
  fileFormat = '';
}
