import { TestBed } from '@angular/core/testing';

import { WSEstudiante } from './ws-estudiante';

describe('WSEstudiante', () => {
  let service: WSEstudiante;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WSEstudiante);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
