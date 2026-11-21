import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Instalacioncomponent } from './instalacioncomponent';

describe('Instalacioncomponent', () => {
  let component: Instalacioncomponent;
  let fixture: ComponentFixture<Instalacioncomponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [Instalacioncomponent],
    }).compileComponents();

    fixture = TestBed.createComponent(Instalacioncomponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
