import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Restablecercomponent } from './restablecercomponent';

describe('Restablecercomponent', () => {
  let component: Restablecercomponent;
  let fixture: ComponentFixture<Restablecercomponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [Restablecercomponent],
    }).compileComponents();

    fixture = TestBed.createComponent(Restablecercomponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
